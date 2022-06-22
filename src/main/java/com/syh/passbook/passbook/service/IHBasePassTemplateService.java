package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.PassTemplate;

public interface IHBasePassTemplateService {
    boolean persistPassTemplateToHBase(PassTemplate passTemplate);
}
