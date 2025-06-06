package utn.back.mordiscoapi.enums;

import java.util.Optional;

public enum EstadoPedido {
    PENDIENTE,EN_PROCESO,EN_CAMINO,RECIBIDO,CANCELADO;

    public Optional<EstadoPedido> getSiguiente() {
        return switch (this) {
            case PENDIENTE -> Optional.of(EN_PROCESO);
            case EN_PROCESO -> Optional.of(EN_CAMINO);
            case EN_CAMINO -> Optional.of(RECIBIDO);
            case RECIBIDO, CANCELADO -> Optional.empty();
        };
    }

    public boolean puedeCambiarA(EstadoPedido nuevoEstado) {
        return nuevoEstado == CANCELADO || getSiguiente().orElse(null) == nuevoEstado;
    }


    public boolean esFinalizado() {
        return this == RECIBIDO || this == CANCELADO;
    }

    public boolean esCancelable() {
        return !esFinalizado();
    }
}
