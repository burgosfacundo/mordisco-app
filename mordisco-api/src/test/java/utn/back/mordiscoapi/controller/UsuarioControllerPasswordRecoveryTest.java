package utn.back.mordiscoapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import utn.back.mordiscoapi.common.constraint.ConstraintViolationMessageResolver;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.GlobalExceptionHandler;
import utn.back.mordiscoapi.service.interf.IUsuarioService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerPasswordRecoveryTest {
    @Mock private IUsuarioService service;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new UsuarioController(service))
                .setControllerAdvice(new GlobalExceptionHandler(new ConstraintViolationMessageResolver()))
                .build();
    }

    @Test
    void recoveryResponsesAreByteIdenticalAcrossProtectedAccountStates() throws Exception {
        byte[] active = recover("active@example.com");
        assertEquals(new String(active), new String(recover("unknown@example.com")));
        assertEquals(new String(active), new String(recover("deactivated@example.com")));
        assertEquals(new String(active), new String(recover("cooldown@example.com")));
    }

    @Test
    void invalidResetTokenStatesHaveTheSameGenericStatusSchemaAndMessage() throws Exception {
        String baseline = resetFailure("malformed");
        for (String token : new String[]{"unknown", "expired", "consumed", "superseded", "deactivated"}) {
            assertEquals(baseline, resetFailure(token));
        }
    }

    private byte[] recover(String email) throws Exception {
        doNothing().when(service).requestPasswordRecovery(any());
        MvcResult result = mockMvc.perform(post("/api/usuarios/recover-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        return result.getResponse().getContentAsByteArray();
    }

    private String resetFailure(String token) throws Exception {
        doThrow(new BadRequestException("El token es inválido o ha expirado"))
                .when(service).resetPassword(any());
        MvcResult result = mockMvc.perform(post("/api/usuarios/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"Password1!\"}"))
                .andReturn();
        assertEquals(400, result.getResponse().getStatus());
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        assertEquals(400, body.get("status").asInt());
        assertEquals("El token es inválido o ha expirado", body.get("message").asText());
        assertEquals(3, body.size());
        return body.get("status").asInt() + ":" + body.get("message").asText() + ":" + body.size();
    }
}
