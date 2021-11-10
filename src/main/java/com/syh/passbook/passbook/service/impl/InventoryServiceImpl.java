package com.syh.passbook.passbook.service.impl;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.dao.MerchantDao;
import com.syh.passbook.passbook.entity.Merchant;
import com.syh.passbook.passbook.mapper.PassTemplateRowMapper;
import com.syh.passbook.passbook.service.IInventoryService;
import com.syh.passbook.passbook.service.IUserPassService;
import com.syh.passbook.passbook.utils.RowKeyGenUtil;
import com.syh.passbook.passbook.vo.InventoryResponse;
import com.syh.passbook.passbook.vo.PassInfo;
import com.syh.passbook.passbook.vo.PassTemplate;
import com.syh.passbook.passbook.vo.PassTemplateInfo;
import com.syh.passbook.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Only return the pass that not taken by user
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {
    private final HbaseTemplate hbaseTemplate;
    private final MerchantDao merchantDao;
    private final IUserPassService userPassService;

    @Autowired
    public InventoryServiceImpl(HbaseTemplate hbaseTemplate,
                                MerchantDao merchantDao,
                                IUserPassService userPassService) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantDao = merchantDao;
        this.userPassService = userPassService;
    }

    @Override
    public Response getInventoryInfo(Long userId) throws Exception {
        Response allUserPass = userPassService.getUserAllPassInfo(userId);
        List<PassInfo> passInfos = (List<PassInfo>) allUserPass.getData();
        List<PassTemplate> excludeObjects = passInfos.stream()
                .map(PassInfo::getPassTemplate)
                .collect(Collectors.toList());
        List<String> excludeIds = new ArrayList<>();
        excludeObjects.forEach(excludeObject -> excludeIds.add(RowKeyGenUtil.genPassTemplateRowKey(excludeObject)));
        return new Response(new InventoryResponse(userId, buildPassTemplateInfo(getAvailablePassTemplate(excludeIds))));
    }

    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIds) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(
            new SingleColumnValueFilter(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                CompareFilter.CompareOp.GREATER,
                new LongComparator(0L)
            )
        );
        filterList.addFilter(
            new SingleColumnValueFilter(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("-1")
            )
        );

        Scan scan = new Scan();
        scan.setFilter(filterList);

        List<PassTemplate> validTemplates = hbaseTemplate.find(
            Constants.PassTemplateTable.TABLE_NAME,
            scan,
            new PassTemplateRowMapper()
        );

        List<PassTemplate> availablePassTemplates = new ArrayList<>();

        Date cur = new Date();
        for (PassTemplate validTemplate: validTemplates) {
            if (excludeIds.contains(RowKeyGenUtil.genPassTemplateRowKey(validTemplate))) {
                continue;
            }
            if (cur.getTime() >= validTemplate.getStart().getTime() &&
                cur.getTime() <= validTemplate.getEnd().getTime()) {
                availablePassTemplates.add(validTemplate);
            }
        }

        return availablePassTemplates;
    }

    private List<PassTemplateInfo> buildPassTemplateInfo(List<PassTemplate> passTemplates) {
        Map<Integer, Merchant> merchantMap = new HashMap<>();

        List<Integer> merchantIds = passTemplates
            .stream()
            .map(PassTemplate::getId)
            .collect(Collectors.toList());

        merchantDao
            .findByIdIn(merchantIds)
            .ifPresent(
                merchantList -> merchantList.forEach(merchant -> merchantMap.put(merchant.getId(), merchant))
            );

        List<PassTemplateInfo> res = new ArrayList<>(passTemplates.size());
        for (PassTemplate passTemplate: passTemplates) {
            Merchant merchant = merchantMap.getOrDefault(passTemplate.getId(), null);
            if (merchant == null) {
                log.error("Merchants Error: {}", passTemplate.getId());
                continue;
            }

            res.add(new PassTemplateInfo(passTemplate, merchant));
        }
        return res;
    }
}
