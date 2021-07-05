package com.winter.common.exception;

import com.winter.common.model.Status;

public class PageException extends AbstractException {

    public PageException(Status status) {
        super(status);
    }

    public PageException(Integer code, String message) {
        super(code, message);
    }
}
