package com.syh.passbook.passbook.vo;

import com.syh.passbook.passbook.entity.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassTemplateInfo extends PassTemplate {
    private PassTemplate passTemplate;
    private Merchant merchant;
}
