package com.demonstration.toolrental.controller;

import com.demonstration.toolrental.controller.exceptions.EmptyResultSetException;
import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ToolRentalControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EmptyResultSetException.class)
    public ResponseEntity<String> handleEmptyResultSetException(EmptyResultSetException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
