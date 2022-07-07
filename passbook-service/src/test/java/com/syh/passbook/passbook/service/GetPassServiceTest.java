package com.syh.passbook.passbook.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syh.passbook.passbook.vo.GetPassRequest;
import com.syh.passbook.passbook.vo.PassTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetPassServiceTest extends AbstractServiceTest {

    @Autowired
    private IGetPassService getPassService;

    @Test
    public void testGetPass() throws Exception {
        PassTemplate target = new PassTemplate();
        target.setMerchantId(20);
        target.setTitle("att family plan");
        target.setHasToken(true);

        System.out.println(new GsonBuilder().create().toJson(
            getPassService.getPass(new GetPassRequest(userId, target))
        ));
    }
}
