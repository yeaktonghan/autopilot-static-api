package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    @Override
    public void sendOTPEmail(String toEmail,String username, String appUrl) throws MessagingException {
        MimeMessage mimeMailMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMailMessage,true);
        helper.setTo(toEmail);
        helper.setSubject("OTP");
        Context context=new Context();
        context.setVariable("username",username);
        context.setVariable("code",appUrl);
        String html=templateEngine.process("email-form",context);
        helper.setText(html,true);
        javaMailSender.send(mimeMailMessage);

    }
}
