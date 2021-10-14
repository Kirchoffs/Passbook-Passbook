package com.syh.passbook.passbook.utils;

import com.syh.passbook.passbook.vo.Feedback;
import com.syh.passbook.passbook.vo.GetPassRequest;
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

    /**
     * Generate RowKey according to the pass request
     * It happens only when the user try to get the pass
     *
     * PassRowKey = reversed(userId) + (Long.MAX_VALUE - timestamp) + PassTemplateRowKey
     */
    public static String genPassRowKey(GetPassRequest request) {
        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString() +
                (Long.MAX_VALUE - System.currentTimeMillis()) +
                genPassTemplateRowKey(request.getPassTemplate());
    }

    public static String genFeedbackRowKey(Feedback feedback) {
        return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString() +
                (Long.MAX_VALUE - System.currentTimeMillis());
    }
}
