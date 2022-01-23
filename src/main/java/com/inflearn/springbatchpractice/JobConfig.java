package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.CustomItemProcessor;
import com.inflearn.springbatchpractice.customer.CustomItemReader;
import com.inflearn.springbatchpractice.customer.CustomItemWriter;
import com.inflearn.springbatchpractice.customer.Customer;
import com.inflearn.springbatchpractice.customer.itemstream.CustomItemStreamReader;
import com.inflearn.springbatchpractice.customer.itemstream.CustomItemStreamWriter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    @JobScope
    Step step1() {
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(3)
                .reader(itemStreamReader())
                .writer(itemStreamWriter())
                .build();
    }

    @Bean
    ItemStreamReader<Customer> itemStreamReader() {
        List<Customer> customers = List.of(Customer.of("user1"), Customer.of("user2"), Customer.of("user3"));
        return new CustomItemStreamReader(customers);
    }

    @Bean
    ItemStreamWriter<Customer> itemStreamWriter() {
        return new CustomItemStreamWriter();
    }

    @Bean
    ItemReader<Customer> itemReader() {
        return new CustomItemReader(
                List.of(Customer.of("user1"), Customer.of("user2"), Customer.of("user3")));
    }

    @Bean
    ItemProcessor<Customer, Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    ItemWriter<Customer> itemWriter() {
        return new CustomItemWriter();
    }

    @Bean
    Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }
}
