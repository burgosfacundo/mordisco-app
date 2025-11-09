package utn.back.mordiscoapi.common.email;

import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

@Component
public final class EmailTemplateBuilder {

    /**
     * Loads an HTML template from the resources directory.
     * @param filename the name of the template file to load (e.g., "activation.html").
     * @return the content of the template as a String.
     */
    private String loadTemplate(String filename) throws InternalServerErrorException {
        String path = "/templates/email/" + filename;
        try (InputStream is = EmailTemplateBuilder.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("No se encontró la plantilla: " + path);
            }
            return new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new InternalServerErrorException("Error al cargar el template HTML: " + path, e);
        }
    }

    /**
     * Replaces placeholders in the template with actual values.
     * Placeholders are in the format ${key}.
     *
     * @param template the HTML template as a String.
     * @param values   a map containing key-value pairs for replacement.
     * @return the template with placeholders replaced by actual values.
     */
    private String replacePlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    /**
     * Builds a password reset email using the provided name and reset link.
     *
     * @param name        the name of the user requesting password reset.
     * @param resetLink   the link for resetting the user's password.
     * @return the formatted password reset email as a String.
     */
    public String buildPasswordResetEmail(String name, String resetLink) throws InternalServerErrorException {
        String template = loadTemplate("reset-password.html");
        return replacePlaceholders(template, Map.of(
                "name", name,
                "resetLink", resetLink
        ));
    }

    /**
     * Builds a password change alert email using the provided name and reset link.
     *
     * @param name        the name of the user whose password has been changed.
     * @return the formatted password change alert email as a String.
     */
    public String buildPasswordChangeAlert(String name,String loginLink) throws InternalServerErrorException {
        String template = loadTemplate("password-changed.html");
        return replacePlaceholders(template, Map.of(
                "name", name,
                "dateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "loginLink", loginLink
        ));
    }

    public String buildNuevoPedidoEmail(String nombre, Long pedidoId, String restaurante,String link)
            throws InternalServerErrorException {
        String template = loadTemplate("nuevo-pedido.html");
        return replacePlaceholders(template, Map.of(
                "nombre", nombre,
                "pedidoId", String.valueOf(pedidoId),
                "restaurante", restaurante,
                "link", link
        ));
    }

    public String buildCambioEstadoPedidoEmail(String nombre, Long pedidoId, String nuevoEstado,String link)
            throws InternalServerErrorException {
        String template = loadTemplate("cambio-estado-pedido.html");
        return replacePlaceholders(template, Map.of(
                "nombre", nombre,
                "pedidoId", String.valueOf(pedidoId),
                "nuevoEstado", nuevoEstado,
                "link", link
        ));
    }

    public String buildPedidoEnCaminoEmail(String nombre, Long pedidoId,String link)
            throws InternalServerErrorException {
        String template = loadTemplate("pedido-en-camino.html");
        return replacePlaceholders(template, Map.of(
                "nombre", nombre,
                "pedidoId", String.valueOf(pedidoId),
                "link", link
        ));
    }
}
