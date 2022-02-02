package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import java.util.List;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

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
    ListItemReader<String> reader() {
        List<String> items = IntStream.range(0, 30)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        return new ListItemReader<>(items);
    }

    @Bean
    @StepScope
    ItemProcessor<String, Customer> processor() {
        return new RetryItemProcessor(retryTemplate());
    }

    RetryTemplate retryTemplate() {
        return new RetryTemplateBuilder()
                .retryOn(RetryableException.class)
                .fixedBackoff(2000)
                .build();
    }
}
