package utn.back.mordiscoapi.model.projection;

import java.time.LocalDate;

public interface PromocionProjection {
    Long getId();
    String getDescripcion();
    Double getDescuento();
    LocalDate getFechaInicio();
    LocalDate getFechaFin();

}
