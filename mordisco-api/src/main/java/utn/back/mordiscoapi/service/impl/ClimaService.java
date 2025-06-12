package utn.back.mordiscoapi.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ClimaService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public boolean estaLloviendo(String ciudad) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                ciudad, apiKey
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Object weatherObj = response.getBody().get("weather");
            if (!(weatherObj instanceof List<?> weatherList)) {
                return false;
            }

            for (Object weatherItem : weatherList) {
                if (weatherItem instanceof Map<?, ?> weather) {
                    Object mainObj = weather.get("main");
                    if (mainObj instanceof String main && "Rain".equalsIgnoreCase(main)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar el clima: " + e.getMessage());
        }

        return false;
    }
}
