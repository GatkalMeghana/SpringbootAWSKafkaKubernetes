package com.forrester.index.exception;

public class ServiceException extends Exception {

    private static final long serialVersionUID = 4471184837407628449L;

    public ServiceException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ServiceException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }
}