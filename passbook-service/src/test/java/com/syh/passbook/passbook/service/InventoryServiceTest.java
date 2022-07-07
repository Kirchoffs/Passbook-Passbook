package com.syh.passbook.passbook.service;

import com.google.gson.GsonBuilder;
import org.apache.hbase.thirdparty.com.google.gson.Gson;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InventoryServiceTest extends AbstractServiceTest {
    @Autowired
    private IInventoryService inventoryService;

    @Test
    public void testGetInventoryInfo() throws Exception {
        System.out.println(new GsonBuilder().create().toJson(inventoryService.getInventoryInfo(userId)));
    }
}
