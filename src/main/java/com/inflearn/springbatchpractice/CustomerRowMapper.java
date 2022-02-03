package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Customer.create(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"),
                rs.getDate("birthDate").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
}
