package com.syh.passbook.passbook.controller;

import com.syh.passbook.passbook.log.LogConstants;
import com.syh.passbook.passbook.log.LogGenerator;
import com.syh.passbook.passbook.service.IFeedbackService;
import com.syh.passbook.passbook.service.IGetPassService;
import com.syh.passbook.passbook.service.IInventoryService;
import com.syh.passbook.passbook.service.IUserPassService;
import com.syh.passbook.passbook.vo.Feedback;
import com.syh.passbook.passbook.vo.GetPassRequest;
import com.syh.passbook.passbook.vo.Pass;
import com.syh.passbook.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {
    private IUserPassService userPassService;
    private IInventoryService inventoryService;
    private IGetPassService getPassService;
    private IFeedbackService feedbackService;
    private HttpServletRequest httpServletRequest;

    @Autowired
    public PassbookController(IUserPassService userPassService,
                              IInventoryService inventoryService,
                              IGetPassService getPassService,
                              IFeedbackService feedbackService,
                              HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.getPassService = getPassService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }

    @ResponseBody
    @GetMapping("/userPassInfo")
    public Response userPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(
            httpServletRequest,
            userId,
            LogConstants.ActionName.USER_PASS_INFO,
            null
        );
        return userPassService.getUserUnusedPassInfo(userId);
    }

    @ResponseBody
    @GetMapping("/userUsedPassInfo")
    public Response userUsedPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(
            httpServletRequest,
            userId,
            LogConstants.ActionName.USER_USED_PASS_INFO,
            null
        );
        return userPassService.getUserUsedPassInfo(userId);
    }

    @ResponseBody
    @PostMapping("/userUsePass")
    Response userUsePass(@RequestBody Pass pass) {
        LogGenerator.genLog(
                httpServletRequest,
                pass.getUserId(),
                LogConstants.ActionName.USER_USE_PASS,
                pass
        );
        return userPassService.userUsePass(pass);
    }

    @ResponseBody
    @GetMapping("/inventoryInfo")
    Response inventoryInfo(Long userId) throws Exception {
        LogGenerator.genLog(
            httpServletRequest,
            userId,
            LogConstants.ActionName.INVENTORY_INFO,
            null
        );
        return inventoryService.getInventoryInfo(userId);
    }

    @ResponseBody
    @PostMapping("/getPass")
    Response getPass(@RequestBody GetPassRequest request) throws Exception {
        LogGenerator.genLog(
            httpServletRequest,
            request.getUserId(),
            LogConstants.ActionName.GET_PASS,
            request
        );
        return getPassService.getPass(request);
    }

    @ResponseBody
    @PostMapping("/createFeedback")
    Response createFeedback(@RequestBody Feedback feedback) {
        LogGenerator.genLog(
            httpServletRequest,
            feedback.getUserId(),
            LogConstants.ActionName.CREATE_FEEDBACK,
            feedback
        );
        return feedbackService.createFeedback(feedback);
    }

    @ResponseBody
    @GetMapping("/getFeedback")
    Response getFeedback(Long userId) {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.CREATE_FEEDBACK,
                null
        );
        return feedbackService.getFeedback(userId);
    }

    @ResponseBody
    @GetMapping("/exception")
    Response exception() throws Exception {
        throw new Exception("Exception");
    }
}
