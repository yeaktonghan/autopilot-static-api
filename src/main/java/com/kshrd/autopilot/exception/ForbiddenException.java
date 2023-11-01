package com.kshrd.autopilot.exception;

public class ForbiddenException extends RuntimeException {
    private String title;
    public ForbiddenException(String title, String message) {
        super(message);
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}

