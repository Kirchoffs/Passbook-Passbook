package com.syh.passbook.passbook.service;

import com.syh.passbook.passbook.vo.Response;
import com.syh.passbook.passbook.vo.User;

public interface IUserService {
    Response createUser(User user) throws Exception;
}
