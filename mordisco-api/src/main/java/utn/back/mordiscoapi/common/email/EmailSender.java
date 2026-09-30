package utn.back.mordiscoapi.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    @Value("${app.mail.from}")
    private String fromAddress;

    /**
     * Sends an HTML email on the caller's delivery boundary.
     *
     * @param to          the recipient's email address.
     * @param subject     the subject of the email.
     * @param htmlContent the HTML content of the email.
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws InternalServerErrorException {
        InternetAddress senderAddress = parseSenderAddress();
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(senderAddress.getAddress(), "Mordisco");
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new InternalServerErrorException("Error al enviar el correo electrónico", e);
        }
    }

    private InternetAddress parseSenderAddress() throws InternalServerErrorException {
        try {
            if (fromAddress == null || fromAddress.isBlank()) {
                throw new jakarta.mail.internet.AddressException("Sender address is not configured");
            }
            InternetAddress address = new InternetAddress(fromAddress, true);
            address.validate();
            return address;
        } catch (MessagingException e) {
            throw new InternalServerErrorException("Error al enviar el correo electrónico", e);
        }
    }
}
