package com.kshrd.autopilot.exception;

import org.springframework.http.HttpStatus;

public class AutoPilotException extends RuntimeException{
    private String title;
    private HttpStatus httpStatus;
    private String url;
    public AutoPilotException(String title,HttpStatus status,String url,String message) {
        super(message);
        this.httpStatus=status;
        this.url=url;
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUrl() {
        return url;
    }
}
