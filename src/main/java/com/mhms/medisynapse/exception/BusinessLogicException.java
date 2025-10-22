package com.mhms.medisynapse.exception;

/**
 * Exception thrown when business logic constraints are violated
 */
public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
