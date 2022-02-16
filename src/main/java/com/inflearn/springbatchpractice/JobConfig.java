package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final int chunkSize = 100;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .listener(new CustomJobExecutionListener())
                .listener(new CustomAnnotationJobListener())
                .build();
    }

    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep().getName(), partitioner())
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step slaveStep() {
        return stepBuilderFactory.get("slaveStep")
                .<Customer, Customer>chunk(chunkSize)
                .reader(pagingItemReader(null, null))
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer");
        return columnRangePartitioner;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> pagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {
        log.info("minValue: {}, maxValue: {}", minValue, maxValue);

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthDate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .dataSource(dataSource)
                .fetchSize(chunkSize)
                .rowMapper(new CustomerRowMapper())
                .queryProvider(queryProvider)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Customer, Customer> customItemProcessor() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item -> Customer.create(item.getId(), item.getFirstName().toUpperCase(),
                item.getLastName().toUpperCase(), item.getBirthDate());
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("insert into customer2 values (:id, :firstName, :lastName, :birthDate)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

}
