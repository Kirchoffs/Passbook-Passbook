package com.syh.passbook.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * The coupon object
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassTemplate {
    // The merchant ID that the coupon belongs to
    private Integer merchantId;
    private String title;
    private String summary;
    private String desc;
    private Long limit;
    private Boolean hasToken;
    private Integer background;
    private Date start;
    private Date end;
}
