package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.PassTemplate;

public interface IHBasePassService {
    boolean distributePassTemplateToHBase(PassTemplate passTemplate);
}
