package com.niyiment.proccessor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {
    private final Job job;
    private final JobLauncher jobLauncher;

//    @Scheduled(cron = "*/20 * * * * *")
//    public void init(){
//        try{
//            JobExecution execution = jobLauncher.run(job,
//                    new JobParametersBuilder()
//                            .addLong("current_time", System.currentTimeMillis())
//                            .toJobParameters()
//            );
//            log.info("Current job status: " + execution.getStatus());
//        } catch (Exception exception) {
//            log.error("Error: {}", exception.getMessage());
//        }
//    }

}
