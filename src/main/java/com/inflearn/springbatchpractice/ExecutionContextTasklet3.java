package com.inflearn.springbatchpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet3 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("step3 was executed");

        Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                .get("name");

        if (name == null) {
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                    .put("name", "user1");

            // NOTE: error 발생시 step3 는 fail 로 종료되고, 재시작 시 step1, step2 를 스킵하고 step3 부터 다시 불러온다!
//            throw new RuntimeException("step2 was failed");
        }

        return RepeatStatus.FINISHED;
    }

}
