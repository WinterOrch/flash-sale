package com.winter.common.exception;

import com.winter.common.model.Status;

public class JsonException extends AbstractException {

    public JsonException(Status status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
