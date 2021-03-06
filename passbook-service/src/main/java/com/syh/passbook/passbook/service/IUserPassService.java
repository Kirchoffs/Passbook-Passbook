package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.Pass;
import com.syh.passbook.passbook.vo.Response;

/**
 * Service that provides the personal information related to passes
 */
public interface IUserPassService {
    // Passes that can be used
    Response getUserUnusedPassInfo(Long userId) throws Exception;

    // Passes that is already been used
    Response getUserUsedPassInfo(Long userId) throws Exception;

    // All passes
    Response getUserAllPassInfo(Long userId) throws Exception;

    Response userUsePass(Pass pass);
}
