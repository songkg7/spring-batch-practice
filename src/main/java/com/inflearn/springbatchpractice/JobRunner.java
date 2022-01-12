package com.inflearn.springbatchpractice;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
    private final Job job;
    private final JobLauncher jobLauncher;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("user", "haril")
                .addDate("date", new Date())
                .addLong("id", 777L)
                .addDouble("double", 1234.12)
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
