package com.niyiment.proccessor.controller;

import com.niyiment.proccessor.config.PersonBatchConsumer;
import com.niyiment.proccessor.config.PersonBatchProducer;
import com.niyiment.proccessor.utility.ConstantUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

@Slf4j
@RequestMapping("/api/v1/persons")
@RestController
@RequiredArgsConstructor
public class PersonController {
    private final JobLauncher jobLauncher;
    private final PersonBatchConsumer personBatchConsumer;
    private final PersonBatchProducer personBatchProducer;


    @PostMapping(path = "/import")
    public ResponseEntity<String> importBatch(@RequestParam("multipartFile") MultipartFile multipartFile) {
        try {
            new File(ConstantUtility.TEMP_IMPORT_DIR).mkdir();

            String filePath = ConstantUtility.TEMP_IMPORT_DIR + multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(filePath));
            jobLauncher.run(personBatchConsumer.personImportJob(), new JobParametersBuilder().addLong("uniqueKey", System.nanoTime()).toJobParameters());

            return ResponseEntity.ok().body("Record consumed successfully");

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Could not create directory");
    }

    @GetMapping(path = "/export")
    public ResponseEntity<String> exportBatch() throws MalformedURLException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(personBatchProducer.personExportJob(),
                new JobParametersBuilder().addLong("uniqueKey", System.nanoTime()).toJobParameters()
        );

        return ResponseEntity.ok().build();

    }

}
