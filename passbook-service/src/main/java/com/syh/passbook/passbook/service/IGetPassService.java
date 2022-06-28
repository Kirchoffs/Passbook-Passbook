package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.GetPassRequest;
import com.syh.passbook.passbook.vo.Response;

/**
 * Service that user get the pass through
 */
public interface IGetPassService {
    Response getPass(GetPassRequest request) throws Exception;
}
