package com.inflearn.springbatchpractice;

import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class CustomAnnotationJobListener {

    @BeforeJob
    public void before() {

    }

    @AfterJob
    public void after() {

    }
}
