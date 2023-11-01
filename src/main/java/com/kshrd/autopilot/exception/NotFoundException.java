package com.kshrd.autopilot.exception;

public class NotFoundException extends RuntimeException{
    private String title;
    public NotFoundException(String title, String message) {
        super(message);
        this.title=title;
    }
}
