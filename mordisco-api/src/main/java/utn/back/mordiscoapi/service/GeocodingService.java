package utn.back.mordiscoapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestClient http;
    private static final String COUNTRY = "Argentina";

    @Cacheable(value = "geocode", key = "#calle + ' ' + #numero + ', ' + #ciudad + ' ' + #cp")
    public Optional<LatLng> geocode(String calle, String numero, String ciudad, String cp) {
        String q = "%s %s, %s %s, %s".formatted(calle, numero, ciudad, cp == null ? "" : cp, COUNTRY);

        try {
            var resp = http.get().uri(uriBuilder -> uriBuilder
                            .scheme("https").host("nominatim.openstreetmap.org").path("/search")
                            .queryParam("format", "json")
                            .queryParam("limit", "1")
                            .queryParam("addressDetails", "0")
                            .queryParam("q", q)
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<Map<String,Object>>>() {});
            var list = resp.getBody();
            if (list == null || list.isEmpty()) return Optional.empty();
            var first = list.getFirst();
            double lat = Double.parseDouble((String) first.get("lat"));
            double lon = Double.parseDouble((String) first.get("lon"));
            return Optional.of(new LatLng(lat, lon));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public record LatLng(double lat, double lng) {}
}
