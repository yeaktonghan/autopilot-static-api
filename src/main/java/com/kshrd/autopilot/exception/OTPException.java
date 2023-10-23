package com.kshrd.autopilot.exception;

public class OTPException extends RuntimeException{
    private String title;
    public OTPException(String title,String message) {
        super(message);
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}
