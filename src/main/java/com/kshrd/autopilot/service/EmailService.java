package com.kshrd.autopilot.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOTPEmail(String toEmail,String username,String appUrl) throws MessagingException;
}
