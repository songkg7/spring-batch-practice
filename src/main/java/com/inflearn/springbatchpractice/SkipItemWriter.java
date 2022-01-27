package com.inflearn.springbatchpractice;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class SkipItemWriter implements ItemWriter<String> {

    private int count = 0;

    @Override
    public void write(List<? extends String> items) throws Exception {
        for (String item : items) {
            if (item.equals("-12")) {
                throw new SkippableException("Write failed count : " + count);
            }
            log.info("ItemWriter : {}", item);
        }
    }
}
