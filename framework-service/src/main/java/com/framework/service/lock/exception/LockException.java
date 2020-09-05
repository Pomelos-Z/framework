package com.framework.service.lock.exception;

import java.text.MessageFormat;

public class LockException extends RuntimeException {

    public LockException() {
        super();
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

}
