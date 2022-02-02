package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

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
                .<String, Customer>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(items -> items.forEach(item -> System.out.println("item = " + item)))
                .faultTolerant()
                .skip(RetryableException.class)
                .skipLimit(2)
                .build();
    }

    @Bean
    @StepScope
    ItemProcessor<String, Customer> processor() {
        return new RetryItemProcessor(retryTemplate());
    }

    @Bean
    @StepScope
    ListItemReader<String> reader() {
        List<String> items = IntStream.range(0, 30)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        return new ListItemReader<>(items);
    }

    @Bean
    SkipPolicy skipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClassMap = new HashMap<>();
        exceptionClassMap.put(RetryableException.class, Boolean.TRUE);
        return new LimitCheckingItemSkipPolicy(2, exceptionClassMap);
    }

    @Bean
    RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClassMap = new HashMap<>();
        exceptionClassMap.put(RetryableException.class, Boolean.TRUE);
        return new SimpleRetryPolicy(2, exceptionClassMap);
    }

    @Bean
    RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClassMap = new HashMap<>();
        exceptionClassMap.put(RetryableException.class, Boolean.TRUE);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClassMap);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
