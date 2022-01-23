package com.inflearn.springbatchpractice.customer;

import lombok.Value;

@Value(staticConstructor = "of")
public class Customer {

    String name;
    String hobby;
    String introduction;

}
