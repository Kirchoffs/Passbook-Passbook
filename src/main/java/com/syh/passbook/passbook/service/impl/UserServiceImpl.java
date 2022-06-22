package com.syh.passbook.passbook.service.impl;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.service.IUserService;
import com.syh.passbook.passbook.vo.Response;
import com.syh.passbook.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private HbaseTemplate hbaseTemplate;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public UserServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response createUser(User user) throws Exception {
        byte[] FAMILY_B = Constants.UserTable.FAMILY_B.getBytes();
        byte[] NAME = Constants.UserTable.NAME.getBytes();
        byte[] AGE = Constants.UserTable.AGE.getBytes();
        byte[] GENDER = Constants.UserTable.GENDER.getBytes();

        byte[] FAMILY_O = Constants.UserTable.FAMILY_O.getBytes();
        byte[] PHONE = Constants.UserTable.PHONE.getBytes();
        byte[] ADDRESS = Constants.UserTable.ADDRESS.getBytes();

        Long curCount = redisTemplate.opsForValue().increment(Constants.USER_COUNT_REDIS_KEY, 1);
        Long userId = genUserId(curCount);

        List<Mutation> data = new ArrayList<>();

        Put put = new Put(Bytes.toBytes(userId));
        put.addColumn(FAMILY_B, NAME, Bytes.toBytes(user.getBaseInfo().getName()));
        put.addColumn(FAMILY_B, AGE, Bytes.toBytes(user.getBaseInfo().getAge()));
        put.addColumn(FAMILY_B, GENDER, Bytes.toBytes(user.getBaseInfo().getGender()));

        put.addColumn(FAMILY_O, PHONE, Bytes.toBytes(user.getOtherInfo().getPhone()));
        put.addColumn(FAMILY_O, ADDRESS, Bytes.toBytes(user.getOtherInfo().getAddress()));

        data.add(put);

        hbaseTemplate.saveOrUpdates(Constants.UserTable.TABLE_NAME, data);
        user.setId(userId);
        return new Response(user);
    }

    private Long genUserId(Long prefix) {
        String suffix = RandomStringUtils.randomNumeric(5);
        return Long.valueOf(prefix + suffix);
    }
}
