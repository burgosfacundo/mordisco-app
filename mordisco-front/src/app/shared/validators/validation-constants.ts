/**
 * Constantes centralizadas de validación para toda la aplicación.
 * Estos valores deben mantenerse sincronizados con el backend (ValidationConstants.java).
 */

// ==================== TELÉFONO ====================
export const TELEFONO_PATTERN = /^\+\d{1,4}(\s?\d){6,14}$/;
export const TELEFONO_MIN_LENGTH = 10;
export const TELEFONO_MAX_LENGTH = 25;

// ==================== PASSWORD ====================
export const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,72}$/;
export const PASSWORD_MIN_LENGTH = 8;
export const PASSWORD_MAX_LENGTH = 72;

// ==================== NOMBRE/APELLIDO ====================
export const NOMBRE_PATTERN = /^[a-zA-Z\u00C0-\u017F\s]+$/;
export const NOMBRE_MIN_LENGTH = 2;
export const NOMBRE_MAX_LENGTH = 50;

// ==================== CIUDAD ====================
export const CIUDAD_PATTERN = /^[a-zA-Z0-9\u00C0-\u017F\s]+$/;
export const CIUDAD_MIN_LENGTH = 2;
export const CIUDAD_MAX_LENGTH = 50;

// ==================== DIRECCIÓN ====================
export const PISO_MAX_LENGTH = 20;
export const DEPTO_MAX_LENGTH = 20;
export const CALLE_MIN_LENGTH = 3;
export const CALLE_MAX_LENGTH = 50;
export const NUMERO_MAX_LENGTH = 10;
export const CODIGO_POSTAL_MAX_LENGTH = 8;
export const REFERENCIAS_MAX_LENGTH = 250;
export const ALIAS_MAX_LENGTH = 50;

// ==================== EMAIL ====================
export const EMAIL_MIN_LENGTH = 5;
export const EMAIL_MAX_LENGTH = 100;

// ==================== IMAGEN ====================
export const IMAGEN_URL_PATTERN = /^https?:\/\/.+/;
export const IMAGEN_URL_CON_EXTENSION_PATTERN = /^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i;
export const IMAGEN_NOMBRE_MAX_LENGTH = 50;

// ==================== CALLE ====================
export const CALLE_PATTERN = /^[A-Za-záéíóúÁÉÍÓÚñÑ0-9 ]+$/;

// ==================== NUMERO DIRECCIÓN ====================
export const NUMERO_PATTERN = /^\d+$/;

// ==================== CÓDIGO POSTAL ====================
export const CODIGO_POSTAL_PATTERN = /^(\d{4}|[A-Z]\d{4}[A-Z]{3})$/;
