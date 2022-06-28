package com.syh.passbook.passbook.service.impl;

import com.google.gson.GsonBuilder;
import com.springboot.hbase.api.HBaseTemplate;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.constant.PassStatus;
import com.syh.passbook.passbook.dao.MerchantDao;
import com.syh.passbook.passbook.entity.Merchant;
import com.syh.passbook.passbook.mapper.PassRowMapper;
import com.syh.passbook.passbook.service.IUserPassService;
import com.syh.passbook.passbook.vo.Pass;
import com.syh.passbook.passbook.vo.PassInfo;
import com.syh.passbook.passbook.vo.PassTemplate;
import com.syh.passbook.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {

    private final HBaseTemplate hbaseTemplate;
    private final MerchantDao merchantDao;

    @Autowired
    public UserPassServiceImpl(HBaseTemplate hbaseTemplate, MerchantDao merchantDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantDao = merchantDao;
    }

    @Override
    public Response getUserUnusedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {
        byte[] rowPrefix = Bytes.toBytes(
            new StringBuilder(String.valueOf(pass.getUserId())).reverse().toString()
        );
        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        filters.add(new PrefixFilter(rowPrefix));
        filters.add(new SingleColumnValueFilter(
            Constants.PassTable.FAMILY_I.getBytes(),
            Constants.PassTable.TEMPLATE_ID.getBytes(),
            CompareFilter.CompareOp.EQUAL,
            Bytes.toBytes(pass.getTemplateId())
        ));
        filters.add(new SingleColumnValueFilter(
            Constants.PassTable.FAMILY_I.getBytes(),
            Constants.PassTable.CONSUMED_DATE.getBytes(),
            CompareFilter.CompareOp.EQUAL,
            Bytes.toBytes("-1")
        ));

        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(
            Constants.PassTable.TABLE_NAME,
            scan,
            new PassRowMapper()
        );

        if (null == passes || passes.size() != 1) {
            log.error("UserUsePass Error: {}", new GsonBuilder().create().toJson(pass));
            return Response.failure("UserUsePass Error");
        }

        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] CONSUMED_DATE =  Constants.PassTable.CONSUMED_DATE.getBytes();

        List<Mutation> data = new ArrayList<>();
        Put put = new Put(passes.get(0).getRowKey().getBytes());
        put.addColumn(
            FAMILY_I,
            CONSUMED_DATE,
            Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()))
        );
        data.add(put);
        hbaseTemplate.saveOrUpdate(Constants.PassTable.TABLE_NAME, data);
        return Response.success();
    }

    private Response getPassInfoByStatus(Long userId, PassStatus status) throws Exception {
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(userId)).reverse().toString());
        CompareFilter.CompareOp compareOp =
                status == PassStatus.UNUSED ?
                CompareFilter.CompareOp.EQUAL : CompareFilter.CompareOp.NOT_EQUAL;

        Scan scan = new Scan();

        List<Filter> filters = new ArrayList<>();
        filters.add(new PrefixFilter(rowPrefix));
        if (status != PassStatus.ALL) {
            filters.add(
                new SingleColumnValueFilter(
                    Constants.PassTable.FAMILY_I.getBytes(),
                    Constants.PassTable.CONSUMED_DATE.getBytes(),
                    compareOp,
                    Bytes.toBytes(-1)
                )
            );
        }

        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());
        Map<String, PassTemplate> passTemplateMap = buildPassTemplateMap(passes);
        Map<Integer, Merchant> merchantMap = buildMerchantMap(new ArrayList<>(passTemplateMap.values()));

        List<PassInfo> result = new ArrayList<>();
        for (Pass pass: passes) {
            PassTemplate passTemplate = passTemplateMap.getOrDefault(pass.getTemplateId(), null);
            if (null == passTemplate) {
                log.error("PassTemplate is null: {}", pass.getTemplateId());
                continue;
            }

            Merchant merchant = merchantMap.getOrDefault(passTemplate.getMerchantId(), null);
            if (null == merchant) {
                log.error("Merchant is null: {}", passTemplate.getMerchantId());
                continue;
            }

            result.add(new PassInfo(pass, passTemplate, merchant));
        }

        return new Response(result);
    }

    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes) throws Exception {
        String[] patterns = new String[] {"yyyy-MM-dd"};

        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);

        List<String> templateIds = passes
                .stream()
                .map(Pass::getTemplateId)
                .collect(Collectors.toList());

        List<Get> templateGets = new ArrayList<>(templateIds.size());
        templateIds.forEach(templateId -> templateGets.add(new Get(Bytes.toBytes(templateId))));

        Result[] templateResults = hbaseTemplate
                .getConnection()
                .getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                .get(templateGets);

        Map<String, PassTemplate> templateIdToObject = new HashMap<>();
        for (Result templateResult: templateResults) {
            PassTemplate passTemplate = new PassTemplate();

            passTemplate.setMerchantId(Bytes.toInt(templateResult.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(templateResult.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(templateResult.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(templateResult.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(templateResult.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(templateResult.getValue(FAMILY_B, BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(templateResult.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(
                    DateUtils.parseDate(Bytes.toString(templateResult.getValue(FAMILY_C, START)), patterns)
            );
            passTemplate.setEnd(
                    DateUtils.parseDate(Bytes.toString(templateResult.getValue(FAMILY_C, END)), patterns)
            );

            templateIdToObject.put(Bytes.toString(templateResult.getRow()), passTemplate);
        }

        return templateIdToObject;
    }

    private Map<Integer, Merchant> buildMerchantMap(List<PassTemplate> passTemplates) {
        Map<Integer, Merchant> merchantMap = new HashMap<>();
        List<Integer> merchantIds = passTemplates
                .stream()
                .map(PassTemplate::getMerchantId)
                .collect(Collectors.toList());

        Optional<List<Merchant>> merchantRes = merchantDao.findByIdIn(merchantIds);
        merchantRes.ifPresent(merchants -> merchants.forEach(merchant -> merchantMap.put(merchant.getId(), merchant)));
        return merchantMap;
    }
}
