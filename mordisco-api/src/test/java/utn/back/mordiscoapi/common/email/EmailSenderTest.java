package utn.back.mordiscoapi.common.email;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(EmailSenderTest.ConfigurationForTest.class)
@TestPropertySource(properties = {
        "spring.mail.username=",
        "app.mail.from=demo-sender@example.test"
})
class EmailSenderTest {
    @Autowired private EmailSender emailSender;
    @Autowired private JavaMailSender mailSender;

    @BeforeEach
    void resetMailSender() {
        reset(mailSender);
    }

    @AfterEach
    void restoreConfiguredFromAddress() {
        ReflectionTestUtils.setField(emailSender, "fromAddress", "demo-sender@example.test");
    }

    @Test
    void usesConfiguredDemoFromAddressWhenSmtpAuthenticationUsernameIsEmpty() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        emailSender.sendHtmlEmail("recipient@example.test", "Demo subject", "<p>Demo content</p>");

        assertEquals("demo-sender@example.test", ((InternetAddress) message.getFrom()[0]).getAddress());
        verify(mailSender).send(message);
    }

    @Test
    void rejectsBlankAndMalformedFromAddressesBeforeCreatingOrSendingMail() {
        for (String invalidAddress : new String[] {"", "   ", "not-an-address"}) {
            ReflectionTestUtils.setField(emailSender, "fromAddress", invalidAddress);

            assertThrows(InternalServerErrorException.class, () ->
                    emailSender.sendHtmlEmail("recipient@example.test", "Demo subject", "<p>Demo content</p>"));
            verifyNoInteractions(mailSender);
        }
    }

    @TestConfiguration
    @Import(EmailSender.class)
    static class ConfigurationForTest {
        @Bean
        JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }
    }
}
