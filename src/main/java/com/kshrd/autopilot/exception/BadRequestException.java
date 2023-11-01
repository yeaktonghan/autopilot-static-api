package com.kshrd.autopilot.exception;

public class BadRequestException extends RuntimeException{
    private String title;
    public BadRequestException(String title, String message) {
        super(message);
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}
