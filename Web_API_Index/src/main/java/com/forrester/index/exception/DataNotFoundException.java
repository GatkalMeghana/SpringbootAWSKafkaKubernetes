package com.forrester.index.exception;

public class DataNotFoundException extends Exception {

    private static final long serialVersionUID = -363634449178464365L;

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
