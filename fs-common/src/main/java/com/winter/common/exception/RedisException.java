package com.winter.common.exception;

import com.winter.common.model.Status;

public class RedisException extends AbstractException {

    public RedisException(Status status) {
        super(status);
    }

    public RedisException(Integer code, String message) {
        super(code, message);
    }

}
