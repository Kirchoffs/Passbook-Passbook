package com.springboot.hbase.service;

import com.google.gson.GsonBuilder;
import com.springboot.hbase.api.HBaseTemplate;
import com.springboot.hbase.mapper.Constants;
import com.springboot.hbase.mapper.Feedback;
import com.springboot.hbase.mapper.FeedbackRowMapper;
import com.springboot.hbase.mapper.FeedbackType;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class FeedbackServiceTest {
    @Autowired
    private HBaseTemplate hbaseTemplate;

    private Long userId;
    private String feedbackRowKey;
    private String feedbackType;
    private String templateId;
    private String feedbackComment;

    @Before
    public void setup() {
        userId = 1073741824L;
        feedbackRowKey = new StringBuilder(String.valueOf(userId)).reverse() + "65536";
        feedbackType = FeedbackType.PASS.name();
        templateId = "Prunus";
        feedbackComment = "Just for Test";
    }

    @Test
    public void testCreateFeedback() {
        Put put = new Put(Bytes.toBytes(feedbackRowKey));

        put.addColumn(
            Bytes.toBytes(Constants.Feedback.FAMILY_I),
            Bytes.toBytes(Constants.Feedback.USER_ID),
            Bytes.toBytes(userId)
        );

        put.addColumn(
            Bytes.toBytes(Constants.Feedback.FAMILY_I),
            Bytes.toBytes(Constants.Feedback.TYPE),
            Bytes.toBytes(feedbackType)
        );

        put.addColumn(
            Bytes.toBytes(Constants.Feedback.FAMILY_I),
            Bytes.toBytes(Constants.Feedback.TEMPLATE_ID),
            Bytes.toBytes(templateId)
        );

        put.addColumn(
            Bytes.toBytes(Constants.Feedback.FAMILY_I),
            Bytes.toBytes(Constants.Feedback.COMMENT),
            Bytes.toBytes(feedbackComment)
        );

        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME, put);
    }

    @Test
    public void testGetFeedback() {
        byte[] reverseUserId = new StringBuilder(String.valueOf(userId)).reverse().toString().getBytes();
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseUserId));

        List<Feedback> feedbackList = hbaseTemplate.find(
                Constants.Feedback.TABLE_NAME,
                scan,
                new FeedbackRowMapper()
        );

        log.info("Feedback in HBase: {}", new GsonBuilder().create().toJson(feedbackList));
    }
}
