package com.syh.passbook.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * The coupon that a user has
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pass {
    private Long userId;

    // Pass(Coupon)'s row key in HBase
    private String rowKey;

    // PassTemplate's row key in HBase
    private String templateId;

    private String token;
    private Date assignedDate;

    // If not null, then it is already been used.
    private Date consumedDate;
}
