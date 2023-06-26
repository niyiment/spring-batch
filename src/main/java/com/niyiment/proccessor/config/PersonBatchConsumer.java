package com.niyiment.proccessor.config;


import com.niyiment.proccessor.domain.entity.Person;
import com.niyiment.proccessor.service.ConsumerJobCompletionListener;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.MalformedURLException;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class PersonBatchConsumer {
    private final DataSource dataSource;
    private final ConsumerJobCompletionListener consumerJobCompletionListener;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    String filePath = ConstantUtility.TEMP_IMPORT_DIR + ConstantUtility.PERSON_FILENAME + ".json";

    @Bean
    public JsonItemReader<Person> consumerReader() throws MalformedURLException {

        return new JsonItemReaderBuilder<Person>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Person.class))
                .resource(new FileUrlResource(filePath))
                .saveState(false)
                .name("consumerReader")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Person> consumerWriter() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<>();
        writer.setSql(ConstantUtility.PERSON_INSERT_QUERY);
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(person -> new MapSqlParameterSource()
                .addValue("id", person.getId())
                .addValue("firstName", person.getFirstName())
                .addValue("lastName", person.getLastName())
                .addValue("phoneNumber", person.getPhoneNumber())
        );

        return writer;
    }

    @Bean
    public Job personImportJob() throws MalformedURLException {
        return new JobBuilder("personImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(consumerJobCompletionListener)
                .flow(consumerStep())
                .end()
                .build();
    }

    @Bean
    public Step consumerStep() throws MalformedURLException {
        return new StepBuilder("consumerStep", jobRepository)
                .<Person, Person> chunk(10, transactionManager)
                .reader(consumerReader())
                .processor(consumerProcessor())
                .writer(consumerWriter())
                .faultTolerant()
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> consumerProcessor() {
        return item -> {
            String firstName =  item.getFirstName().substring(0, 1).toUpperCase()
                    + item.getFirstName().substring(1);
            String lastName = item.getLastName().substring(1).toUpperCase();
            log.info("Last name: " + lastName);

            return Person.builder()
                    .id(item.getId())
                    .firstName(firstName)
                    .lastName(lastName)
                    .phoneNumber(item.getPhoneNumber())
                    .build();
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
}
