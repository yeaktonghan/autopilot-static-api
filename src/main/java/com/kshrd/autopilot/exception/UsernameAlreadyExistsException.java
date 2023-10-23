package com.kshrd.autopilot.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    private String title;
    public UsernameAlreadyExistsException(String title,String message) {
        super(message);
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}

