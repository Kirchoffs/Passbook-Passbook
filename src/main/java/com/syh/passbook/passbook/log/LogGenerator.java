package com.syh.passbook.passbook.log;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class LogGenerator {
    public static void genLog(HttpServletRequest request, Long userId, String action, Object info) {
        log.info(
                new Gson().toJson(
                        new LogObject(
                                action,
                                userId,
                                System.currentTimeMillis(),
                                request.getRemoteAddr(),
                                info
                        )
                )
        );
    }
}
