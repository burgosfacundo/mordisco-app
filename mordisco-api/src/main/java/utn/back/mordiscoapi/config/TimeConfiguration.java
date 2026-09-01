package utn.back.mordiscoapi.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
@Configuration
public class TimeConfiguration {
    @Bean
    public Clock utcClock() {
        return Clock.systemUTC();
    }
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
    public static LocalDateTime toUtcMicroseconds(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).withNano((instant.getNano() / 1_000) * 1_000);
    }
}
