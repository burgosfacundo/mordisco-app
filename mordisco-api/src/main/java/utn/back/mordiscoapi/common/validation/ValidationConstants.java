package utn.back.mordiscoapi.common.validation;

/**
 * Constantes centralizadas de validación para toda la aplicación.
 * Estos valores deben mantenerse sincronizados con el frontend.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        // Utility class
    }

    // ==================== TELÉFONO ====================
    public static final String TELEFONO_PATTERN = "^\\+\\d{1,4}(\\s?\\d){6,14}$";
    public static final int TELEFONO_MIN_LENGTH = 10;
    public static final int TELEFONO_MAX_LENGTH = 25;
    public static final String TELEFONO_MESSAGE = "Teléfono inválido. Formato: +54 9 223 123456";

    // ==================== PASSWORD ====================
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$";
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 72;
    public static final String PASSWORD_MESSAGE = "La contraseña debe tener 8-72 caracteres, con al menos una mayúscula, una minúscula, un número y un carácter especial";

    // ==================== NOMBRE/APELLIDO ====================
    public static final String NOMBRE_PATTERN = "^[a-zA-Z\\u00C0-\\u017F\\s]+$";
    public static final int NOMBRE_MIN_LENGTH = 2;
    public static final int NOMBRE_MAX_LENGTH = 50;
    public static final String NOMBRE_MESSAGE = "Solo se permiten letras y espacios";

    // ==================== CIUDAD ====================
    public static final String CIUDAD_PATTERN = "^[a-zA-Z0-9\\u00C0-\\u017F\\s]+$";
    public static final int CIUDAD_MIN_LENGTH = 2;
    public static final int CIUDAD_MAX_LENGTH = 50;
    public static final String CIUDAD_MESSAGE = "Solo se permiten letras, números y espacios";

    // ==================== DIRECCIÓN ====================
    public static final int PISO_MAX_LENGTH = 20;
    public static final int DEPTO_MAX_LENGTH = 20;
    public static final int CALLE_MAX_LENGTH = 50;
    public static final int NUMERO_MAX_LENGTH = 5;
    public static final int CODIGO_POSTAL_MAX_LENGTH = 10;
    public static final int REFERENCIAS_MAX_LENGTH = 255;
    public static final int ALIAS_MAX_LENGTH = 50;

    // ==================== EMAIL ====================
    public static final int EMAIL_MAX_LENGTH = 100;

    // ==================== IMAGEN ====================
    public static final String IMAGEN_URL_PATTERN = "^https?:\\/\\/.+";
    public static final String IMAGEN_URL_CON_EXTENSION_PATTERN = "^https?:\\/\\/.+\\.(jpg|jpeg|png|gif|webp)(\\?.*)?$";
    public static final int IMAGEN_NOMBRE_MAX_LENGTH = 50;
}
