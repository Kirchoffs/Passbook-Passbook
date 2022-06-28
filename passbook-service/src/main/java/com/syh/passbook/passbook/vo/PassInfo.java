package com.syh.passbook.passbook.vo;

import com.syh.passbook.passbook.entity.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassInfo {
    private Pass pass;
    private PassTemplate passTemplate;
    private Merchant merchant;
}
