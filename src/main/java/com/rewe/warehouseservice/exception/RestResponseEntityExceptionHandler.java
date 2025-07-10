package com.rewe.warehouseservice.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class, MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ProblemDetail handleConstraintViolationException(
            Exception exception, WebRequest request) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        problemDetail.setTitle("Constraint Violation Exception");

        return problemDetail;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail handleObjectNotFoundException(ObjectNotFoundException exception, WebRequest request) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        problemDetail.setTitle("Not Found Exception");

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAll(
            Exception exception, WebRequest request) {
        ProblemDetail problemDetail
                = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setInstance(URI.create(request.getContextPath()));
        problemDetail.setTitle("Something went wrong!");

        return problemDetail;
    }
}
