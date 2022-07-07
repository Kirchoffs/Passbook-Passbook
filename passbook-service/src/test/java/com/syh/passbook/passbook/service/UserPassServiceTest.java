package com.syh.passbook.passbook.service;

import com.google.gson.GsonBuilder;
import com.syh.passbook.passbook.vo.Pass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest {
    @Autowired
    private IUserPassService userPassService;

    @Test
    public void testGetUserAllPassInfo() throws Exception {
        System.out.println(new GsonBuilder().create().toJson(userPassService.getUserAllPassInfo(userId)));
    }

    @Test
    public void testGetUserUsedPassInfo() throws Exception {
        System.out.println(new GsonBuilder().create().toJson(userPassService.getUserUsedPassInfo(userId)));
    }

    @Test
    public void testGetUserUnusedPassInfo() throws Exception {
        System.out.println(new GsonBuilder().create().toJson(userPassService.getUserUnusedPassInfo(userId)));
    }

    @Test
    public void testUserUsePass() throws Exception {
        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("855ecb5eb3f59f9d1c68ee521003316a");

        System.out.println(new GsonBuilder().create().toJson(userPassService.userUsePass(pass)));
    }
}
