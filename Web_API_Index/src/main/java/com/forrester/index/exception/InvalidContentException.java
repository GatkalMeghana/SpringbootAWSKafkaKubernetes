package com.forrester.index.exception;

public class InvalidContentException extends Exception{
    public InvalidContentException(String message) {
        super(message);
    }

    public InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
