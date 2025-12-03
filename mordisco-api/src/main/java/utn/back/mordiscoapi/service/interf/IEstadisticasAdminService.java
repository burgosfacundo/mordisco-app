package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.model.dto.estadisticas.AdminEstadisticasDTO;

public interface IEstadisticasAdminService {
    /**
     * Obtiene las estadísticas generales de la plataforma para administradores
     * @return Estadísticas del admin
     */
    AdminEstadisticasDTO getEstadisticas();
}
