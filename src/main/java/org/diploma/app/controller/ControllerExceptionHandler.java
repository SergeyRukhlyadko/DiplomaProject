package org.diploma.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.diploma.app.controller.response.ResponseBadRequestBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus()
    @ExceptionHandler(Exception.class)
    public void defaultHandler(Exception e) {
        e.printStackTrace();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(200).body(new ResponseErrorBody(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach((error) -> errors.put(error.getPropertyPath().toString(), error.getMessage()));
        return ResponseEntity.status(400).body(new ResponseErrorBody(errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        e.printStackTrace();
        return ResponseEntity.status(400).body(new ResponseBadRequestBody("The query contains an unsupported variable"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(400).body(new ResponseBadRequestBody(e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<?> handle(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(400).body(new ResponseBadRequestBody("Not supported content type"));
    }

    @ExceptionHandler
    ResponseEntity<?> handle(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(new ResponseBadRequestBody("Invalid request body"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException() {
        return ResponseEntity.status(400).body(new ResponseBadRequestBody("Attached file more than 5MB"));
    }
}
