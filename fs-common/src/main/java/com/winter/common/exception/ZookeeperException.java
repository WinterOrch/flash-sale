package com.winter.common.exception;

import com.winter.common.model.Status;

public class ZookeeperException extends AbstractException {
    public ZookeeperException(Status status) {
        super(status);
    }

    public ZookeeperException(Integer code, String message) {
        super(code, message);
    }

}
