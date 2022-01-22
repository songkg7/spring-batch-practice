package com.inflearn.springbatchpractice.customer;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        return Customer.changeName(customer.getName().toUpperCase());
    }
}
