package com.syh.passbook.passbook.service.impl;

import com.google.gson.GsonBuilder;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.mapper.PassTemplateRowMapper;
import com.syh.passbook.passbook.service.IGetPassService;
import com.syh.passbook.passbook.utils.RowKeyGenUtil;
import com.syh.passbook.passbook.vo.GetPassRequest;
import com.syh.passbook.passbook.vo.PassTemplate;
import com.syh.passbook.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GetPassServiceImpl implements IGetPassService {
    private HbaseTemplate hbaseTemplate;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public GetPassServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate stringRedisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Response getPass(GetPassRequest request) throws Exception {
        PassTemplate passTemplate;
        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());

        try {
            passTemplate = hbaseTemplate.get(
                Constants.PassTemplateTable.TABLE_NAME,
                passTemplateId,
                new PassTemplateRowMapper()
            );
        } catch (Exception ex) {
            log.error("Get PassTemplate Error: {}", new GsonBuilder().create().toJson(request.getPassTemplate()));
            return Response.failure("Get PassTemplate Error");
        }

        if (passTemplate.getLimit() <= 0 && passTemplate.getLimit() != -1) {
            log.error("PassTemplate Limit Max: {}", new GsonBuilder().create().toJson(request.getPassTemplate()));
            return Response.failure("PassTemplate Limit Max");
        }

        Date cur = new Date();
        if (!(cur.getTime() >= passTemplate.getStart().getTime() && cur.getTime() < passTemplate.getEnd().getTime())) {
            log.error("PassTemplate ValidTime Error: {}", new GsonBuilder().create().toJson(request.getPassTemplate()));
            return Response.failure("PassTemplate ValidTime Error");
        }

        if (passTemplate.getLimit() != -1) {
            List<Mutation> data = new ArrayList<>();
            byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
            Put put = new Put(Bytes.toBytes(passTemplateId));
            put.addColumn(
                FAMILY_C,
                LIMIT,
                Bytes.toBytes(passTemplate.getLimit() - 1)
            );
            data.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, data);
        }

        if (!addPassForUser(request, passTemplate.getId(), passTemplateId)) {
            return Response.failure("GetPass Failure");
        }

        return Response.success();
    }

    private boolean addPassForUser(GetPassRequest request,
                                   Integer merchantId,
                                   String passTemplateId) throws Exception {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CONSUMED_DATE = Constants.PassTable.CONSUMED_DATE.getBytes();

        List<Mutation> data = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genPassRowKey(request)));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        if (request.getPassTemplate().getHasToken()) {
            String token = stringRedisTemplate.opsForSet().pop(passTemplateId);
            if (token == null) {
                log.error("Token not exist: {}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantId, passTemplateId, token);
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
        } else {
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));
        }

        put.addColumn(
            FAMILY_I,
            ASSIGNED_DATE,
            Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()))
        );
        put.addColumn(FAMILY_I, CONSUMED_DATE, Bytes.toBytes("-1"));

        data.add(put);

        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, data);

        return true;
    }

    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token) throws Exception {
        Files.write(
            Paths.get(
                Constants.TOKEN_DIR,
                String.valueOf(merchantsId),
                passTemplateId + Constants.USED_TOKEN_SUFFIX
            ),
            (token + "\n").getBytes(),
            StandardOpenOption.APPEND
        );
    }
}
