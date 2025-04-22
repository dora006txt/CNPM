package ptithcm.edu.pharmacy.service;

// Remove SendGrid imports
// import com.sendgrid.*;
// import com.sendgrid.helpers.mail.Mail;
// import com.sendgrid.helpers.mail.objects.Content;
// import com.sendgrid.helpers.mail.objects.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Add Autowired
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException; // Add MailException
import org.springframework.mail.SimpleMailMessage; // Add SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender; // Add JavaMailSender
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// Remove IOException import if no longer needed
// import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // Remove SendGrid specific properties
    // @Value("${sendgrid.api.key}")
    // private String sendGridApiKey;

    // Inject JavaMailSender
    @Autowired
    private JavaMailSender mailSender;

    // Inject the 'from' email address from properties
    @Value("${spring.mail.username}") // Use the standard Spring Mail property
    private String fromEmailAddress;

    @Async // Optional: Send email asynchronously
    public void sendPasswordResetEmail(String toEmail, String subject, String body) {
        // Ensure 'from' email is configured (using spring.mail.username)
        if (fromEmailAddress == null || fromEmailAddress.isEmpty()) {
            log.error("Spring Mail 'from' email address (spring.mail.username) is not configured.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmailAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Password reset email sent successfully via Gmail to {}", toEmail);

        } catch (MailException ex) {
            log.error("Error sending password reset email via Gmail to {}: {}", toEmail, ex.getMessage(), ex);
            // Consider how to handle failures (e.g., retry mechanism, specific logging)
        } catch (Exception e) {
            log.error("Unexpected error sending password reset email via Gmail to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}