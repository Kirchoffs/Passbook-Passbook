package com.springboot.hbase.service;

import com.springboot.hbase.api.HBaseTemplate;
import com.springboot.hbase.mapper.Constants;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private HBaseTemplate hbaseTemplate;

    private Long userId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String address;

    @Before
    public void setup() {
        userId = 1073741824L;
        name = "Ben";
        age = 27;
        gender = "Male";
        phone = "2147483648";
        address = "5905 Wilshire Blvd, Los Angeles, CA 90036";
    }

    @Test
    public void testCreateUser() {
        Put put = new Put(Bytes.toBytes(userId));

        put.addColumn(
            Bytes.toBytes(Constants.UserTable.FAMILY_B),
            Bytes.toBytes(Constants.UserTable.NAME),
            Bytes.toBytes(name)
        );
        put.addColumn(
            Bytes.toBytes(Constants.UserTable.FAMILY_B),
            Bytes.toBytes(Constants.UserTable.AGE),
            Bytes.toBytes(age)
        );
        put.addColumn(
            Bytes.toBytes(Constants.UserTable.FAMILY_B),
            Bytes.toBytes(Constants.UserTable.GENDER),
            Bytes.toBytes(gender)
        );

        put.addColumn(
            Bytes.toBytes(Constants.UserTable.FAMILY_O),
            Bytes.toBytes(Constants.UserTable.PHONE),
            Bytes.toBytes(phone)
        );
        put.addColumn(
            Bytes.toBytes(Constants.UserTable.FAMILY_O),
            Bytes.toBytes(Constants.UserTable.ADDRESS),
            Bytes.toBytes(address)
        );

        List<Mutation> operations = new ArrayList<>();
        operations.add(put);

        hbaseTemplate.saveOrUpdate(Constants.UserTable.TABLE_NAME, operations);
    }
}
