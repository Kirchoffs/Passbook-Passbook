package com.syh.passbook.passbook.service;

import com.google.gson.GsonBuilder;
import com.syh.passbook.passbook.constant.FeedbackType;
import com.syh.passbook.passbook.vo.Feedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest {
    @Autowired
    private IFeedbackService feedbackService;

    @Test
    public void testCreateFeedbackForApp() {
        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.name());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("Good");

        System.out.println(new GsonBuilder().create().toJson(feedbackService.createFeedback(appFeedback)));
    }

    @Test
    public void testCreateFeedbackForPass() {
        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.name());
        passFeedback.setTemplateId("855ecb5eb3f59f9d1c68ee521003316a");
        passFeedback.setComment("Interesting");

        System.out.println(new GsonBuilder().create().toJson(feedbackService.createFeedback(passFeedback)));
    }

    @Test
    public void testGetFeedback() {
        System.out.println(new GsonBuilder().create().toJson(feedbackService.getFeedback(userId)));
    }
}
