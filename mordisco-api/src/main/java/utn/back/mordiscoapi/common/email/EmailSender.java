package utn.back.mordiscoapi.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String email;

    /**
     * Sends an HTML email asynchronously.
     *
     * @param to          the recipient's email address.
     * @param subject     the subject of the email.
     * @param htmlContent the HTML content of the email.
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws InternalServerErrorException {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(email, "Mordisco");
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new InternalServerErrorException("Error al enviar el correo electrónico", e);
        }
    }
}
