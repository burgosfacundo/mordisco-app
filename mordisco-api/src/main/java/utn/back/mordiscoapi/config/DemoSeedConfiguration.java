package utn.back.mordiscoapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import utn.back.mordiscoapi.demo.DemoSeedService;

@Configuration(proxyBeanMethods = false)
@Profile("demo-seed")
public class DemoSeedConfiguration {

    private final Environment environment;

    public DemoSeedConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    ApplicationRunner demoSeedRunner(DemoSeedService demoSeedService) {
        return args -> demoSeedService.seed();
    }

    @PostConstruct
    void validateActivation() {
        boolean production = environment.acceptsProfiles(Profiles.of("prod"));
        boolean schemaBootstrap = environment.acceptsProfiles(Profiles.of("schema-bootstrap"));
        boolean development = environment.acceptsProfiles(Profiles.of("dev"));

        if (!production || !schemaBootstrap || development) {
            throw new IllegalStateException(
                    "demo-seed requires prod and schema-bootstrap and cannot be combined with dev"
            );
        }
    }
}
