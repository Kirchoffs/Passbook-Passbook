package com.syh.passbook.passbook.controller;

import com.syh.passbook.passbook.log.LogConstants;
import com.syh.passbook.passbook.log.LogGenerator;
import com.syh.passbook.passbook.service.IUserService;
import com.syh.passbook.passbook.vo.Response;
import com.syh.passbook.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {

    private IUserService userService;
    private HttpServletRequest httpServletRequest;

    @Autowired
    public CreateUserController(IUserService userService,
                                HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }

    @ResponseBody
    @PostMapping("createUser")
    Response createUser(@RequestBody User user) throws Exception {
        LogGenerator.genLog(
            httpServletRequest,
            -1L,
            LogConstants.ActionName.CREATE_USER,
            user
        );

        return userService.createUser(user);
    }
}
