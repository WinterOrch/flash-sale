package com.winter.flashsale.exphandler.exception;

import com.winter.flashsale.exphandler.Status;

public class RedisException extends AbstractException {

    public RedisException(Status status) {
        super(status);
    }

    public RedisException(Integer code, String message) {
        super(code, message);
    }

}
