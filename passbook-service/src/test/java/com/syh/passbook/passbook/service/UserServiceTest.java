package com.syh.passbook.passbook.service;

import com.google.gson.GsonBuilder;
import com.syh.passbook.passbook.vo.Response;
import com.syh.passbook.passbook.vo.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();

        user.setBaseInfo(new User.BaseInfo("Ben", 25, "m"));
        user.setOtherInfo(new User.OtherInfo("2147483647", "1223 W 29th Street"));

        Response resp = userService.createUser(user);
        Assert.assertEquals(0, resp.getErrorCode().intValue());
        System.out.println(new GsonBuilder().create().toJson(resp));
    }
}
