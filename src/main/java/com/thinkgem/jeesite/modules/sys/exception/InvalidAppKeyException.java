package com.thinkgem.jeesite.modules.sys.exception;

public class InvalidAppKeyException extends RuntimeException {

    public InvalidAppKeyException() {
    }

    public InvalidAppKeyException(String message) {
        super(message);
    }

    public InvalidAppKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAppKeyException(Throwable cause) {
        super(cause);
    }
}
