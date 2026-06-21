package com.jash.taskservice.domain.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRecoveryEmail(String targetEmail, String fullName, String resetToken) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(targetEmail);
            mailMessage.setSubject("🛡️ PG Premium Suite - Secure Account Recovery Request");
            mailMessage.setText(
                "Hello " + fullName + ",\n\n" +
                "We received a request to reset your password access credentials.\n" +
                "Click the link below or copy it directly into your device workspace to authorize changes:\n\n" +
                "https://pgpremium.suite/auth/reset-password?token=" + resetToken + "\n\n" +
                "⚠️ This secure authentication link updates tracking registers and expires in exactly 15 minutes.\n" +
                "If you did not authorize this action, please secure your credentials immediately.\n\n" +
                "Best regards,\nSecurity Ops - PG Premium Suite Team"
            );

            mailSender.send(mailMessage);
        } catch (Exception emailError) {
            // Log background exception securely without blocking user interactions
            System.err.println("Background email delivery exception encountered: " + emailError.getMessage());
        }
    }
}