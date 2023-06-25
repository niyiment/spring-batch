package com.niyiment.proccessor.domain.mapper;

import com.niyiment.proccessor.domain.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {

        Person p = new Person();
        p.setId(rs.getLong("id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setPhoneNumber(rs.getString("phone_number"));

        return p;
    }
}
