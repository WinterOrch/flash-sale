package com.winter.flashsale.exphandler.exception;

import com.winter.flashsale.exphandler.Status;

public class ZookeeperException extends AbstractException {
    public ZookeeperException(Status status) {
        super(status);
    }

    public ZookeeperException(Integer code, String message) {
        super(code, message);
    }

}
