package utn.back.mordiscoapi.model.enums;

/**
 * Tipo de descuento que puede aplicar una promoción
 */
public enum TipoDescuento {
    /**
     * Descuento porcentual (ej: 10%, 20%, 50%)
     */
    PORCENTAJE,
    
    /**
     * Descuento de monto fijo (ej: $500, $1000)
     */
    MONTO_FIJO
}
