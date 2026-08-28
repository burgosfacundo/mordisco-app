package utn.back.mordiscoapi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("prod")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.websocket-allowed-origins=http://localhost:4200",
                "server.ssl.enabled=false"
        }
)
class WebSocketTransportIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String WEBSOCKET_KEY = "dGhlIHNhbXBsZSBub25jZQ==";
    private static final String WEBSOCKET_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    @LocalServerPort
    private int port;

    @Value("${app.websocket-allowed-origins}")
    private List<String> configuredOrigins;

    @Test
    void developmentUsesTheConfiguredExactOriginForBothTransports() {
        AppProperties properties = new AppProperties();
        properties.setWebsocketAllowedOrigins(List.of("http://localhost:4200"));

        assertDoesNotThrow(() -> properties.validateWebSocketOrigins("dev"));
        assertEquals(List.of("http://localhost:4200"), properties.getWebsocketAllowedOrigins());
    }

    @Test
    void productionFailsClosedForMissingBlankOrWildcardOrigins() {
        for (List<String> invalid : List.of(
                List.<String>of(),
                List.of(" "),
                List.of("*"),
                List.of("https://trusted.example", "*"))) {
            AppProperties properties = new AppProperties();
            properties.setWebsocketAllowedOrigins(invalid);
            assertThrows(IllegalStateException.class,
                    () -> properties.validateWebSocketOrigins("prod"));
        }
    }

    @Test
    void rejectsWildcardOriginsOutsideProductionToo() {
        AppProperties properties = new AppProperties();
        properties.setWebsocketAllowedOrigins(List.of("*"));

        assertThrows(IllegalStateException.class,
                () -> properties.validateWebSocketOrigins("dev"));
    }

    @Test
    void nativeAndSockJsTransportsApplyTheSameExactOriginDecision() throws Exception {
        String allowed = configuredOrigins.getFirst();
        String denied = "https://untrusted.example";

        HttpResponse sockJsInfo = sockJsInfo(allowed);
        assertEquals(200, sockJsInfo.status());
        assertEquals(allowed, sockJsInfo.header("access-control-allow-origin"));
        JsonNode info = OBJECT_MAPPER.readTree(sockJsInfo.body());
        assertNotNull(info);
        assertTrue(info.isObject());
        assertTrue(info.has("cookie_needed"));
        assertTrue(info.get("cookie_needed").isBoolean());

        assertEquals(403, sockJsInfo(denied).status());

        HttpResponse nativeHandshake = nativeHandshake(allowed);
        assertEquals(101, nativeHandshake.status());
        assertEquals("websocket", nativeHandshake.header("upgrade").toLowerCase(Locale.ROOT));
        assertTrue(nativeHandshake.header("connection").toLowerCase(Locale.ROOT).contains("upgrade"));
        assertEquals(expectedWebSocketAccept(), nativeHandshake.header("sec-websocket-accept"));

        assertEquals(403, nativeHandshake(denied).status());
    }

    @Test
    void productionSockJsPostIsNotRejectedByServletCsrf() throws Exception {
        String allowed = configuredOrigins.getFirst();
        String sessionPath = "/api/ws/000/issue23session";
        HttpResponse xhrPoll = request("POST", sessionPath + "/xhr", allowed, "", false);
        assertEquals(200, xhrPoll.status());
        assertTrue(xhrPoll.body().startsWith("o"));

        HttpResponse xhrSend = request("POST", sessionPath + "/xhr_send", allowed, "[]", false);
        assertEquals(204, xhrSend.status());
        assertEquals("", xhrSend.body());
        assertFalse(xhrSend.status() == 404 || xhrSend.status() == 500);
    }

    @Test
    void productionCsrfStillRejectsAnUnrelatedProtectedPost() throws Exception {
        HttpResponse response = request("POST", "/api/pedidos/save", configuredOrigins.getFirst(), "{}", false);

        assertEquals(403, response.status());
    }

    private HttpResponse sockJsInfo(String origin) throws Exception {
        return request("GET", "/api/ws/info", origin, "", false);
    }

    private HttpResponse nativeHandshake(String origin) throws Exception {
        return request("GET", "/api/ws", origin, "", true);
    }

    private HttpResponse request(String method, String path, String origin, String body, boolean upgrade) throws Exception {
        try (Socket socket = new Socket("127.0.0.1", port);
             OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII);
             InputStream input = socket.getInputStream()) {
            socket.setSoTimeout(5_000);
            writer.write(method + " " + path + " HTTP/1.1\r\n");
            writer.write("Host: 127.0.0.1:" + port + "\r\n");
            writer.write("Origin: " + origin + "\r\n");
            writer.write("Connection: " + (upgrade ? "Upgrade" : "close") + "\r\n");
            if (upgrade) {
                writer.write("Upgrade: websocket\r\n");
                writer.write("Sec-WebSocket-Version: 13\r\n");
                writer.write("Sec-WebSocket-Key: " + WEBSOCKET_KEY + "\r\n");
            }
            if ("POST".equals(method)) {
                writer.write("Content-Type: application/json\r\n");
                writer.write("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n");
            }
            writer.write("\r\n");
            writer.write(body);
            writer.flush();

            String statusLine = readAsciiLine(input);
            assertNotNull(statusLine);
            String[] statusParts = statusLine.split(" ", 3);
            assertTrue(statusParts.length >= 2, "Expected an HTTP status line");
            int status = Integer.parseInt(statusParts[1]);
            Map<String, String> headers = new LinkedHashMap<>();
            String headerLine;
            while ((headerLine = readAsciiLine(input)) != null && !headerLine.isEmpty()) {
                int separator = headerLine.indexOf(':');
                assertTrue(separator > 0, "Expected a valid HTTP header");
                headers.put(headerLine.substring(0, separator).toLowerCase(Locale.ROOT),
                        headerLine.substring(separator + 1).trim());
            }

            return new HttpResponse(status, headers, upgrade ? "" : readResponseBody(input, headers));
        }
    }

    private String readResponseBody(InputStream input, Map<String, String> headers) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        if ("chunked".equalsIgnoreCase(headers.get("transfer-encoding"))) {
            String chunkHeader;
            while ((chunkHeader = readAsciiLine(input)) != null) {
                int size = Integer.parseInt(chunkHeader.split(";", 2)[0], 16);
                if (size == 0) {
                    while (!readAsciiLine(input).isEmpty()) {
                        // Consume chunk trailers.
                    }
                    break;
                }
                byte[] chunk = input.readNBytes(size);
                assertEquals(size, chunk.length, "Expected complete HTTP chunk");
                body.write(chunk);
                assertEquals("", readAsciiLine(input), "Expected HTTP chunk terminator");
            }
        } else if (headers.containsKey("content-length")) {
            int size = Integer.parseInt(headers.get("content-length"));
            byte[] response = input.readNBytes(size);
            assertEquals(size, response.length, "Expected complete HTTP response body");
            body.write(response);
        }
        return body.toString(StandardCharsets.UTF_8);
    }

    private String readAsciiLine(InputStream input) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int next;
        while ((next = input.read()) != -1) {
            if (next == '\n') {
                break;
            }
            if (next != '\r') {
                line.write(next);
            }
        }
        return next == -1 && line.size() == 0 ? null : line.toString(StandardCharsets.US_ASCII);
    }

    private String expectedWebSocketAccept() throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-1")
                .digest((WEBSOCKET_KEY + WEBSOCKET_ACCEPT_GUID).getBytes(StandardCharsets.US_ASCII));
        return java.util.Base64.getEncoder().encodeToString(digest);
    }

    private record HttpResponse(int status, Map<String, String> headers, String body) {
        private String header(String name) {
            String value = headers.get(name);
            assertNotNull(value, "Expected response header " + name);
            return value;
        }
    }
}
