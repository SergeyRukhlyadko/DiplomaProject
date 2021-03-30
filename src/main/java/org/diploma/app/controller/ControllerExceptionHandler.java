package org.diploma.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.diploma.app.controller.response.ResponseBadRequestBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus()
    @ExceptionHandler
    public void handle(Exception e) {
        log.error(e.getMessage(), e);
    }

    @ExceptionHandler
    public ResponseErrorBody handle(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseErrorBody(errors);
    }

    @ExceptionHandler
    public ResponseErrorBody handle(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach((error) -> errors.put(error.getPropertyPath().toString(), error.getMessage()));
        return new ResponseErrorBody(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseBadRequestBody handle(MethodArgumentTypeMismatchException e) {
        return new ResponseBadRequestBody("The query contains an unsupported variable");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseBadRequestBody handle(MissingServletRequestParameterException e) {
        return new ResponseBadRequestBody(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseBadRequestBody handle(HttpMediaTypeNotSupportedException e) {
        return new ResponseBadRequestBody("Not supported content type");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseBadRequestBody handle(HttpMessageNotReadableException e) {
        return new ResponseBadRequestBody("Invalid request body");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseBadRequestBody handle(MaxUploadSizeExceededException e) {
        return new ResponseBadRequestBody("Attached file more than 5MB");
    }
}
