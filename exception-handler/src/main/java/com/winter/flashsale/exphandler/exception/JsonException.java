package com.winter.flashsale.exphandler.exception;

import com.winter.flashsale.exphandler.Status;

public class JsonException extends AbstractException {

    public JsonException(Status status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
