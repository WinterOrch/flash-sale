package com.winter.flashsale.exphandler.exception;

import com.winter.flashsale.exphandler.Status;

public class UserException extends AbstractException {
    public UserException(Status status) {
        super(status);
    }

    public UserException(Integer code, String message) {
        super(code, message);
    }

}
