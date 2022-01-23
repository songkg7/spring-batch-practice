package com.inflearn.springbatchpractice.customer.itemstream;

import com.inflearn.springbatchpractice.customer.Customer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

@Slf4j
public class CustomItemStreamWriter implements ItemStreamWriter<Customer> {

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter.open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter.update");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("CustomItemStreamWriter.close");
    }

    @Override
    public void write(List<? extends Customer> items) throws Exception {
        items.forEach(item -> log.info("item = {}", item));
    }
}
