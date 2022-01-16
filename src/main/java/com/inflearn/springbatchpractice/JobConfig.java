package com.inflearn.springbatchpractice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
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
                .on("COMPLETED").to(step3())
                .from(step1())
                .on("FAILED").to(step2())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    throw new RuntimeException("step1 was failed.");
//                    return RepeatStatus.FINISHED;
                })
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
}
