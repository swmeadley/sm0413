package com.demonstration.toolrental.controller.exceptions;

public class EmptyResultSetException extends Exception {
    /**
     * Exception to handle no tool found
     */
    private static final long serialVersionUID = 2L;
    public EmptyResultSetException(String message) {
        super(message);
    }
}
