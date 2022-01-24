package com.inflearn.springbatchpractice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBatchTest
@SpringBootTest(classes = { JobConfig.class, TestBatchConfig.class })
class JobConfigTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;

    @BeforeEach
    void clearJobExecution() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void test_1() {

    }
}