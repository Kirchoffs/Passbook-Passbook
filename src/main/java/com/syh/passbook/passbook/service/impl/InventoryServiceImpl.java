package com.syh.passbook.passbook.service.impl;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.mapper.PassTemplateRowMapper;
import com.syh.passbook.passbook.service.IInventoryService;
import com.syh.passbook.passbook.utils.RowKeyGenUtil;
import com.syh.passbook.passbook.vo.PassTemplate;
import com.syh.passbook.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Only return the pass that not taken by user
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {
    private final HbaseTemplate hbaseTemplate;

    public InventoryServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Response getInventoryInfo(Long userId) throws Exception {
        return null;
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
}
