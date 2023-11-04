package com.valloyd.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

	@Test
	void mapRow() throws SQLException {
		// Given
		CustomerRowMapper customerRowMapper = new CustomerRowMapper();

		ResultSet resultSet = mock(ResultSet.class);
		when(resultSet.getInt("id")).thenReturn(1);
		when(resultSet.getString("name")).thenReturn("Tap");
		when(resultSet.getString("email")).thenReturn("tap@gmail.com");
		when(resultSet.getInt("age")).thenReturn(28);

		// When
		Customer actual = customerRowMapper.mapRow(resultSet, 1);

		// Then
		Customer expected = new Customer(1, "Tap", "tap@gmail.com", 28);

		assertThat(actual).isEqualTo(expected);
	}
}