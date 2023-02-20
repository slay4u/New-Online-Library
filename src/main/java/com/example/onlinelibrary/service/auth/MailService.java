package com.example.onlinelibrary.service.auth;

import com.example.onlinelibrary.domain.NotificationEmail;
import com.example.onlinelibrary.exception.MailActivationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailContentBuilder mailContentBuilder;

    // Send email verification asynchronously
    @Async
    public void sendEmail(NotificationEmail notificationEmail) {
        String recipient = notificationEmail.getRecipient();
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("onlinelibraryapi@email.com");
            messageHelper.setTo(recipient);
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };

        try {
            javaMailSender.send(mimeMessagePreparator);
            log.info("Activation email sent to " + recipient);
        } catch (MailException e) {
            log.error("Exception thrown: {}", e.getMessage());
            throw new MailActivationException("Error occurred sending email to " + recipient);
        }
    }
}
