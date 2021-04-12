package com.demonstration.toolrental.controller.exceptions;

public class InvalidRequestException extends Exception {
    /**
     * Exception to handle bad/invalid requests
     */
    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
