package com.inflearn.springbatchpractice.customer;

import lombok.Value;

@Value(staticConstructor = "of")
public class Customer {

    String name;

    static Customer changeName(String name) {
        return new Customer(name);
    }

}
