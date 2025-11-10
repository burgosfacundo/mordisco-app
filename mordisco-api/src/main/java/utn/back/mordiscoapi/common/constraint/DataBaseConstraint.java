package utn.back.mordiscoapi.common.constraint;

import java.util.Arrays;

public enum DataBaseConstraint {

    CALIFICACION_RESTAURANTE("UK_calificacion_usuario_restaurante", "Ya calificaste este restaurante."),
    IMAGEN_URL("UK_imagen_url", "La URL de la imagen ya está en uso."),
    RESTAURANTE_USUARIO("UK_restaurante_usuario", "Ese usuario ya está asignado a un restaurante."),
    RESTAURANTE_RAZON_SOCIAL("UK_restaurante_razon_social", "La razón social del restaurante ya está en uso."),
    RESTAURANTE_IMAGEN("UK_restaurante_imagen", "La imagen ya está en uso por otro restaurante."),
    RESTAURANTE_DIRECCION("UK_restaurante_direccion", "La dirección ya está en uso por otro restaurante."),
    MENU_RESTAURANTE("UK_menu_restaurante","El restaurante ya tiene un menu creado"),
    PRODUCTO_NOMBRE("UK_producto_nombre", "El nombre del producto no puede ser mayor a 50 caracteres."),
    PRODUCTO_IMAGEN("UK_producto_imagen", "La imagen ya está en uso por otro producto o restaurante."),
    USUARIO_TELEFONO("UK_usuario_telefono", "El teléfono ya está registrado."),
    USUARIO_EMAIL("UK_usuario_email", "El email ya está registrado."),
    NOT_NULL("not-null", "Faltan datos obligatorios."),
    UNIQUE_CONSTRAINT("unique constraint", "Valor duplicado en un campo único."),
    FOREIGN_KEY("foreign key", "Referencia a un dato inexistente."),
    REFERENCED_COLUMN("referencedColumn", "Valor referenciado no encontrado.");

    private final String key;
    private final String message;

    DataBaseConstraint(String key, String message) {
        this.key = key;
        this.message = message;
    }

    /**
     * Resolves a detailed error message to a user-friendly message based on the database constraint.
     *
     * @param detailedMessage the detailed error message from the database.
     * @return a user-friendly error message.
     */
    public static String resolveMessage(String detailedMessage) {
        return Arrays.stream(values())
                .filter(constraint -> detailedMessage.toLowerCase()
                        .contains(constraint.key.toLowerCase())
                )
                .map(constraint -> constraint.message)
                .findFirst()
                .orElse("Error de integridad en los datos ingresados.");
    }

}