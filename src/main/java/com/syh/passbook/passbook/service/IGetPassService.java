package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.GetPassRequest;
import com.syh.passbook.passbook.vo.Response;

public interface IGetPassService {
    Response getPass(GetPassRequest request) throws Exception;
}
