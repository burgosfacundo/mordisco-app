package utn.back.mordiscoapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
@Profile("!prod")
@EnableScheduling
public class SchedulingConfiguration {
}
