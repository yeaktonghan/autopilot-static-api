package com.kshrd.autopilot.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionError extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String,Object> body= new  LinkedHashMap();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",status.value());
        body.put("error",ex.getFieldError().getDefaultMessage());
        return ResponseEntity.ok(body);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail forbiddenException(ForbiddenException forbiddenException){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, forbiddenException.getMessage()
        );
        problemDetail.setType(URI.create("http://localhost:8080/errors/"));
        problemDetail.setTitle(forbiddenException.getTitle());
        return  problemDetail;
    }
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFoundException(NotFoundException e){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage()
        );
        problemDetail.setType(URI.create("http://localhost:8080/errors/"));
        problemDetail.setTitle(problemDetail.getTitle());
        return problemDetail;
    }
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail badRequestException(BadRequestException badRequestException){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, badRequestException.getMessage()
        );
        problemDetail.setTitle(badRequestException.getTitle());
        problemDetail.setType(URI.create("http://localhost:8080/errors/"));
        return problemDetail;
    }
    @ExceptionHandler(AutoPilotException.class)
    public ProblemDetail autoPilotException(AutoPilotException exception){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(
                exception.getHttpStatus(),exception.getMessage()
        );
        problemDetail.setTitle(exception.getTitle());
        problemDetail.setType(URI.create(exception.getUrl()));
        return problemDetail;
    }
}
