package com.winter.flashsale.exphandler.exception;

import com.winter.flashsale.exphandler.Status;

public class PageException extends AbstractException {

    public PageException(Status status) {
        super(status);
    }

    public PageException(Integer code, String message) {
        super(code, message);
    }
}
