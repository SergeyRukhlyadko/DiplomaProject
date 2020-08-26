package org.diploma.app.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus()
    @ExceptionHandler(Exception.class)
    public void defaultHandler(Exception e) {
        e.printStackTrace();
    }
}
