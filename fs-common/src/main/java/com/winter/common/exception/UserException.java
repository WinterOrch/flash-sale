package com.winter.common.exception;

import com.winter.common.model.Status;

public class UserException extends AbstractException {
    public UserException(Status status) {
        super(status);
    }

    public UserException(Integer code, String message) {
        super(code, message);
    }

}
