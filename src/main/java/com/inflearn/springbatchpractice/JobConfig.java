package com.inflearn.springbatchpractice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobRegistry jobRegistry;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    @JobScope
    Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(5)
                .reader(new ItemReader<>() {
                    int i = 0;

                    @Override
                    public String read()
                            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        if (i == 1) {
                            log.error("illegalArgumentException");
                            throw new IllegalArgumentException("this exception is skipped");
                        }
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor((ItemProcessor<String, String>) item -> {
                    log.error("illegalStateException");
                    throw new IllegalStateException("this exception is retried");
                })
                .writer(items -> items.forEach(item -> log.info("item = {}", item)))
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(2)
                .retry(IllegalStateException.class)
                .retryLimit(3)
                .build();
    }
}
