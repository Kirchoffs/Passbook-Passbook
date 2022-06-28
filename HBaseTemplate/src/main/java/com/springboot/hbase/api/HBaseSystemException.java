package com.springboot.hbase.api;


import com.springboot.hbase.conf.HBaseProperties;
import org.springframework.dao.UncategorizedDataAccessException;

public class HBaseSystemException extends UncategorizedDataAccessException {
    public HBaseSystemException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public HBaseSystemException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }
}
