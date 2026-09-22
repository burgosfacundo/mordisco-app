package utn.back.mordiscoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import utn.back.mordiscoapi.config.StartupTimingConfiguration;

@SpringBootApplication
@EnableCaching
public class MordiscoApiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MordiscoApiApplication.class);
        application.addListeners(new StartupTimingConfiguration.ApplicationStartupTimingListener());
        application.run(args);
    }

}
