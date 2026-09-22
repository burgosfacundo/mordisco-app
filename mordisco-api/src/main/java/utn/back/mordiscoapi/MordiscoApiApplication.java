package utn.back.mordiscoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MordiscoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MordiscoApiApplication.class, args);
    }

}
