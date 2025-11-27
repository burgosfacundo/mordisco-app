package utn.back.mordiscoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EstadoPedido {
    // Estados comunes
    PENDIENTE("Pendiente", "Pedido creado, esperando confirmación del restaurante"),
    EN_PREPARACION("En Preparación", "El restaurante está preparando tu pedido"),

    // Estados para RETIRO_POR_LOCAL
    LISTO_PARA_RETIRAR("Listo para Retirar", "Tu pedido está listo, puedes pasar a retirarlo"),

    // Estados para DELIVERY
    LISTO_PARA_ENTREGAR("Listo para Entregar", "Pedido listo para que el repartidor lo retire"),
    EN_CAMINO("En Camino", "El repartidor está llevando tu pedido"),

    // Estado final común
    COMPLETADO("Completado", "Pedido finalizado exitosamente"),

    // Estado de cancelación
    CANCELADO("Cancelado", "Pedido cancelado");

    private final String displayName;
    private final String descripcion;

    // Estados válidos por tipo de entrega
    private static final Set<EstadoPedido> ESTADOS_RETIRO = EnumSet.of(
            PENDIENTE, EN_PREPARACION, LISTO_PARA_RETIRAR, COMPLETADO, CANCELADO
    );

    private static final Set<EstadoPedido> ESTADOS_DELIVERY = EnumSet.of(
            PENDIENTE, EN_PREPARACION, LISTO_PARA_ENTREGAR, EN_CAMINO, COMPLETADO, CANCELADO
    );

    /**
     * Verifica si este estado es válido para un tipo de entrega
     */
    public boolean esValidoParaTipoEntrega(TipoEntrega tipoEntrega) {
        return switch (tipoEntrega) {
            case RETIRO_POR_LOCAL -> ESTADOS_RETIRO.contains(this);
            case DELIVERY -> ESTADOS_DELIVERY.contains(this);
        };
    }

    /**
     * Obtiene los estados válidos para un tipo de entrega
     */
    public static Set<EstadoPedido> obtenerEstadosValidosPara(TipoEntrega tipoEntrega) {
        return switch (tipoEntrega) {
            case RETIRO_POR_LOCAL -> ESTADOS_RETIRO;
            case DELIVERY -> ESTADOS_DELIVERY;
        };
    }

    /**
     * Verifica si se puede cambiar de este estado a otro, considerando el tipo de entrega
     */
    public boolean puedeCambiarA(EstadoPedido nuevoEstado, TipoEntrega tipoEntrega) {
        // No se puede cambiar si ya está completado o cancelado
        if (this == COMPLETADO || this == CANCELADO) {
            return false;
        }

        // Validar que ambos estados sean válidos para el tipo de entrega
        if (!this.esValidoParaTipoEntrega(tipoEntrega) || !nuevoEstado.esValidoParaTipoEntrega(tipoEntrega)) {
            return false;
        }

        // Definir transiciones válidas según tipo de entrega
        return switch (tipoEntrega) {
            case RETIRO_POR_LOCAL -> puedeCambiarRetiroLocal(nuevoEstado);
            case DELIVERY -> puedeCambiarDelivery(nuevoEstado);
        };
    }

    private boolean puedeCambiarRetiroLocal(EstadoPedido nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
            case EN_PREPARACION -> nuevoEstado == LISTO_PARA_RETIRAR || nuevoEstado == CANCELADO;
            case LISTO_PARA_RETIRAR -> nuevoEstado == COMPLETADO || nuevoEstado == CANCELADO;
            default -> false;
        };
    }

    private boolean puedeCambiarDelivery(EstadoPedido nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
            case EN_PREPARACION -> nuevoEstado == LISTO_PARA_ENTREGAR || nuevoEstado == CANCELADO;
            case LISTO_PARA_ENTREGAR -> nuevoEstado == EN_CAMINO || nuevoEstado == CANCELADO;
            case EN_CAMINO -> nuevoEstado == COMPLETADO || nuevoEstado == CANCELADO;
            default -> false;
        };
    }

    /**
     * Verifica si el pedido puede ser cancelado en este estado
     */
    public boolean esCancelable() {
        return this == PENDIENTE || this == EN_PREPARACION || this == LISTO_PARA_RETIRAR || this == LISTO_PARA_ENTREGAR;
    }

    /**
     * Obtiene el siguiente estado lógico según el tipo de entrega
     */
    public EstadoPedido obtenerSiguienteEstado(TipoEntrega tipoEntrega) {
        return switch (tipoEntrega) {
            case RETIRO_POR_LOCAL -> switch (this) {
                case PENDIENTE -> EN_PREPARACION;
                case EN_PREPARACION -> LISTO_PARA_RETIRAR;
                case LISTO_PARA_RETIRAR -> COMPLETADO;
                default -> this;
            };
            case DELIVERY -> switch (this) {
                case PENDIENTE -> EN_PREPARACION;
                case EN_PREPARACION -> LISTO_PARA_ENTREGAR;
                case LISTO_PARA_ENTREGAR -> EN_CAMINO;
                case EN_CAMINO -> COMPLETADO;
                default -> this;
            };
        };
    }

    /**
     * Verifica si este es un estado final
     */
    public boolean esFinal() {
        return this == COMPLETADO || this == CANCELADO;
    }

    /**
     * Verifica si este estado requiere acción del repartidor
     */
    public boolean requiereRepartidor() {
        return this == LISTO_PARA_ENTREGAR || this == EN_CAMINO;
    }
}