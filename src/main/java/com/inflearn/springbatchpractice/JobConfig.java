package com.inflearn.springbatchpractice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final int chunkSize = 10;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .start(step1())
                .on("FAILED").to(step2())
                .on("FAILED").stop()
                .from(step1())
                .on("*").to(step3()).next(step4())
                .from(step2())
                .on("*").to(step5())
                .end()
                .build();
    }

    @Bean
    public Flow flowA() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
        flowBuilder
                .start(step1())
                .next(step2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .<String, String>chunk(chunkSize)
                .reader(new ListItemReader<>(List.of("die", "aloud", "division", "inch", "rather")))
                .processor((ItemProcessor<String, String>) String::toUpperCase)
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .<Integer, Integer>chunk(chunkSize)
                .reader(new ListItemReader<>(List.of(377, 54, 960, 651, 274)))
                .processor((ItemProcessor<Integer, Integer>) item -> 0)
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    @Bean
    public Flow flowB() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowB");
        flowBuilder
                .start(step4())
                .next(step5())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public Step step5() {
        return stepBuilderFactory.get("step5")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public Step step6() {
        return stepBuilderFactory.get("step6")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }
}
