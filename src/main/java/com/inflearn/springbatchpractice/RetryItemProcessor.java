package com.inflearn.springbatchpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RetryItemProcessor implements ItemProcessor<String, String> {

    private int count = 0;

    @Override
    public String process(String item) throws Exception {
        if (item.equals("2") || item.equals("3")) {
            count++;
            log.info("count: {}", count);
            throw new RetryableException("fail count : " + count);
        }
        return item;
    }
}
