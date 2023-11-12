package com.kshrd.autopilot.exception;

public class ConflictException extends RuntimeException {
    private String title;
    public ConflictException(String title, String message) {
        super(message);
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}

