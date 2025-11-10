package utn.back.mordiscoapi.model.dto.pago;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMercadoPagoRequest {

    private String action;

    @JsonProperty("api_version")
    private String apiVersion;

    private WebhookData data;

    @JsonProperty("date_created")
    private String dateCreated;

    private Long id;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    private String type;

    @JsonProperty("user_id")
    private String userId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookData {
        private String id;
    }
}
