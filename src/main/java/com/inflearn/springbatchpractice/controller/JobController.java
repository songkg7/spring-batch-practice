package com.inflearn.springbatchpractice.controller;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    @PostMapping("/batch/start")
    public String start(@RequestBody JobRequest jobRequest)
            throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);
            System.out.println("job.getName() = " + job.getName());
            jobOperator.start(job.getName(), "id=" + jobRequest.getId());
        }

        return "batch is started";
    }

    @PostMapping("/batch/stop")
    public String stop()
            throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);
            System.out.println("job.getName() = " + job.getName());

            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
            JobExecution jobExecution = runningJobExecutions.iterator().next();
            jobOperator.stop(jobExecution.getId());
        }

        return "batch is stopped";
    }

    @PostMapping("/batch/restart")
    public String restart() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);
            System.out.println("job.getName() = " + job.getName());

            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
            JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);

            jobOperator.stop(lastJobExecution.getId());
        }

        return "batch is restarted";
    }
}
