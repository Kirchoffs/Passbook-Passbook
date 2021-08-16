package com.syh.passbook.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controller response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Integer errorCode = 0;
    private String errorMsg = "";
    private Object data;

    public Response(Object data) {
        this.data = data;
    }

    public static Response success() {
        return new Response();
    }

    public static Response failure(String errorMsg) {
        return new Response(-1, errorMsg, null);
    }
}
