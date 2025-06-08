package utn.back.mordiscoapi.exception;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConstraintViolationMessageResolver {

    private static final Map<String, String> constraintMessages = Map.ofEntries(
            Map.entry("UK_calificacion_usuario_restaurante", "Ya calificaste este restaurante."),

            Map.entry("UK_imagen_url", "La URL de la imagen ya está en uso."),

            Map.entry("UK_restaurante_usuario", "Ese usuario ya está asignado a un restaurante."),
            Map.entry("UK_restaurante_razon_social", "La razón social del restaurante ya está en uso."),
            Map.entry("UK_restaurante_imagen", "La imagen ya está en uso por otro restaurante."),
            Map.entry("UK_restaurante_direccion", "La dirección ya está en uso por otro restaurante."),

            Map.entry("UK_producto_nombre", "El nombre del producto no puede ser mayor a 50 caracteres."),
            Map.entry("UK_producto_imagen", "La imagen ya está en uso por otro producto o restaurante."),

            Map.entry("UK_usuario_telefono", "El teléfono ya está registrado."),
            Map.entry("UK_usuario_email", "El email ya está registrado."),

            Map.entry("not-null", "Faltan datos obligatorios."),
            Map.entry("unique constraint", "Valor duplicado en un campo único."),

            // Fallback genérico para claves foráneas
            Map.entry("foreign key", "Referencia a un dato inexistente."),
            Map.entry("referencedColumn", "Valor referenciado no encontrado.")
    );


    public String resolveMessage(String detailedMessage) {
        for (Map.Entry<String, String> entry : constraintMessages.entrySet()) {
            if (detailedMessage.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return "Error de integridad en los datos ingresados.";
    }
}
