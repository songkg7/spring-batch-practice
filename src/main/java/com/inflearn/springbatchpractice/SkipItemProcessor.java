package com.inflearn.springbatchpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SkipItemProcessor implements ItemProcessor<String, String> {

    private int count = 0;

    @Override
    public String process(String item) throws Exception {

        if (item.equals("6") || item.equals("7")) {
            log.info("ItemProcess : {}", item);
            throw new SkippableException("Process Failed count: " + count);
        }
        log.info("ItemProcess : {}", item);
        return String.valueOf(Integer.parseInt(item) * -1);
    }
}
