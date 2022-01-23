package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        return Customer.of(fieldSet.readString(0), fieldSet.readString(1), fieldSet.readString(2));
    }
}
