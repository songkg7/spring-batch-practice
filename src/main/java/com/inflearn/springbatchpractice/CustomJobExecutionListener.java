package com.inflearn.springbatchpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class CustomJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job is started.");
        log.info("jobName: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long start = jobExecution.getStartTime().getTime();
        long end = jobExecution.getEndTime().getTime();

        log.info("총 소요시간: {}", String.valueOf(end - start));
    }
}
