package com.inflearn.springbatchpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        stepExecution.getExecutionContext().putString("name", "user1");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus exitStatus = stepExecution.getExitStatus();
        BatchStatus batchStatus = stepExecution.getStatus();
        String name = (String) stepExecution.getExecutionContext().get("name");

        log.info("exitStatus: {}", exitStatus);
        log.info("batchStatus: {}", batchStatus);
        log.info("name: {}", name);
        return ExitStatus.COMPLETED;
    }
}
