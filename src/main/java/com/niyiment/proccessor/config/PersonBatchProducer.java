package com.niyiment.proccessor.config;

import com.niyiment.proccessor.domain.entity.Person;
import com.niyiment.proccessor.domain.mapper.PersonRowMapper;
import com.niyiment.proccessor.service.ProducerJobCompletionListener;
import com.niyiment.proccessor.utility.ConstantUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class PersonBatchProducer {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ProducerJobCompletionListener jobCompletionListener;

    public ItemProcessor<Person, Person> processor() {
        return item -> {
            String firstName =  item.getFirstName().substring(0, 1).toUpperCase()
                    + item.getFirstName().substring(1);
            String lastName = item.getLastName().substring(0, 1).toUpperCase()
                    + item.getLastName().substring(1);

            return Person.builder()
                    .id(item.getId())
                    .firstName(firstName)
                    .lastName(lastName)
                    .phoneNumber(item.getPhoneNumber())
                    .build();
        };
    }

    @Bean
    public Job personExportJob() throws MalformedURLException {
        return new JobBuilder("personJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() throws MalformedURLException {
        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(3, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .listener(jobCompletionListener)
                .build();
    }

    @Bean
    public JsonFileItemWriter<Person> writer() throws MalformedURLException {
        String directory = ConstantUtility.TEMP_EXPORT_DIR;
        boolean isCreated = new File(directory).mkdir();
        String filePath = ConstantUtility.TEMP_BASE_DIR + ConstantUtility.PERSON_FILENAME + ".json";
       if (isCreated) {
           filePath = directory + ConstantUtility.PERSON_FILENAME + ".json";
       }

        return new JsonFileItemWriterBuilder<Person>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileUrlResource(filePath))
                .saveState(false)
                .name("writer")
                .build();
    }

    public JdbcCursorItemReader<Person> reader() {
        return new JdbcCursorItemReaderBuilder<Person>()
                .dataSource(dataSource)
                .name("reader")
                .saveState(false)
                .sql(ConstantUtility.PERSON_FETCH_QUERY)
                .rowMapper(new PersonRowMapper())
                .build();
    }

}

