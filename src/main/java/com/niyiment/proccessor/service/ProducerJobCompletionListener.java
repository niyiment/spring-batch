package com.niyiment.proccessor.service;

import com.niyiment.proccessor.domain.entity.Person;
import com.niyiment.proccessor.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProducerJobCompletionListener implements JobExecutionListener {
    private final PersonRepository repository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Batch export completed successfully");

            List<Person> people = repository.findAll();
            people.forEach(person -> log.info("Records in database: {}", person));
        }
    }
}
