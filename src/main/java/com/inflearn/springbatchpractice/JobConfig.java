package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
                .start(step1())
                .build();
    }

    @Bean
    @JobScope
    Step step1() {
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(5)
                .reader(flatFileReader())
                .writer(items -> items.forEach(item -> log.info("item = {}", item)))
                .build();
    }

    // NOTE: 반환 유형에 주의, ItemReader 에는 open() method 가 없기 때문에 생각한대로 동작하지 않을 수 있다.
    // 다형성을 위해 더 상위 클래스로 반환하는 것은 좋으나, 배치에서는 정확한 클래스를 리턴해주는 것이 좋을수도 있다.
    @Bean
    @StepScope
    FlatFileItemReader<Customer> flatFileReader() {
        DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>(new DelimitedLineTokenizer(), new CustomerFieldSetMapper());

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileReader")
                .resource(new ClassPathResource("/customer.csv"))
                .linesToSkip(1)
                .lineMapper(defaultLineMapper)
                .build();
    }

    @Bean
    BeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
