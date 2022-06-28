package com.syh.passbook.passbook.mapper;

import com.springboot.hbase.api.RowMapper;
import com.syh.passbook.passbook.constant.Constants;
import com.syh.passbook.passbook.vo.Pass;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class PassRowMapper implements RowMapper<Pass> {
    private static byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();

    private static byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
    private static byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
    private static byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
    private static byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
    private static byte[] CONSUMED_DATE = Constants.PassTable.CONSUMED_DATE.getBytes();

    @Override
    public Pass mapRow(Result result, int rowNum) throws Exception {

        Pass pass = new Pass();

        pass.setUserId(Bytes.toLong(result.getValue(FAMILY_I, USER_ID)));
        pass.setTemplateId(Bytes.toString(result.getValue(FAMILY_I, TEMPLATE_ID)));
        pass.setToken(Bytes.toString(result.getValue(FAMILY_I, TOKEN)));

        String[] patterns = new String[] {"yyyy-DD-dd"};
        pass.setAssignedDate(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_I, ASSIGNED_DATE)), patterns));

        // Consumed Date
        String consumedDateStr = Bytes.toString(result.getValue(FAMILY_I, CONSUMED_DATE));
        if (consumedDateStr.equals("-1")) {
            pass.setConsumedDate(null);
        } else {
            pass.setConsumedDate(DateUtils.parseDate(consumedDateStr, patterns));
        }

        pass.setRowKey(Bytes.toString(result.getRow()));

        return pass;
    }
}
