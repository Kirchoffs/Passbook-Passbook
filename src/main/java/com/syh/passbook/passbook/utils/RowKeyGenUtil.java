package com.syh.passbook.passbook.utils;

import com.syh.passbook.passbook.vo.Feedback;
import com.syh.passbook.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class RowKeyGenUtil {
    public static String genPassTemplateRowKey(PassTemplate passTemplate) {

        String passInfo = passTemplate.getId() + "_" + passTemplate.getTitle();
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("GenPassTemplateRowKey: {}, {}", passInfo, rowKey);

        return rowKey;
    }

    public static String genFeedbackRowKey(Feedback feedback) {
        return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString() +
                (Long.MAX_VALUE - System.currentTimeMillis());
    }
}
