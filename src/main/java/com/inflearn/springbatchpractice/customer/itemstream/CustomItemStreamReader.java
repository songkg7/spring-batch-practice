package com.inflearn.springbatchpractice.customer.itemstream;

import com.inflearn.springbatchpractice.customer.Customer;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@Slf4j
public class CustomItemStreamReader implements ItemStreamReader<Customer> {

    private final List<Customer> customers;
    private int index;
    private boolean restart = false;

    public CustomItemStreamReader(List<Customer> customers) {
        this.customers = new ArrayList<>(customers);
        this.index = 0;
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Customer customer = null;
        if (index < customers.size()) {
            customer = customers.get(index);
            index++;
        }

        // NOTE: 재시작시 이미 저장되어 있는 인덱스를 가져와서 처리하게 된다.
        if (index == 2 && !restart) {
            throw new RuntimeException("Restart is required.");
        }

        return customer;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("index")) {
            index = executionContext.getInt("index");
            restart = true;
        } else {
            index = 0;
            executionContext.put("index", index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("close");
    }
}
