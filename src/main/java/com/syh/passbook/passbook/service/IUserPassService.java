package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.Pass;
import com.syh.passbook.passbook.vo.Response;

public interface IUserPassService {
    // Passes that can be used
    Response getUserPassInfo(Long userId) throws Exception;

    // Passes that is already been used
    Response getUserUsedPassInfo(Long userId) throws Exception;

    // All passes
    Response getUserAllPassInfo(Long userId) throws Exception;

    Response useUserPass(Pass pass);
}
