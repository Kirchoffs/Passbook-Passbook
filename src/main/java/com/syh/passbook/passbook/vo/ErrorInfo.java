package com.syh.passbook.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo<T> {
    public static final Integer ERROR = -1;
    private Integer code;
    private String message;
    private String url;
    private T data;
}
