package com.amigoscode.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Alex");
        when(rs.getString("email")).thenReturn("alex@wp.pl");
        when(rs.getInt("age")).thenReturn(20);

        //When
        Customer actual = customerRowMapper.mapRow(rs, 1);

        //Then
        Customer expected = new Customer(1L, "Alex", "alex@wp.pl", 20);
        assertThat(actual).isEqualTo(expected);

    }
}