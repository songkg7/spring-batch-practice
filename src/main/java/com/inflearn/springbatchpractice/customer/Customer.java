package com.inflearn.springbatchpractice.customer;

import java.time.LocalDateTime;
import lombok.Value;

@Value(staticConstructor = "create")
public class Customer {
    Long id;
    String firstName;
    String lastName;
    LocalDateTime birthDate;
}
