package com.syh.passbook.passbook.service;


import com.syh.passbook.passbook.vo.Response;

public interface IInventoryService {
    Response getInventoryInfo(Long userId) throws Exception;
}
