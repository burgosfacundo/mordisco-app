package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.repartidor.*;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RepartidorRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IRepartidorService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepartidorServiceImpl implements IRepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    public List<RepartidorResponseDTO> findDisponiblesCercanos(
            Double latitud, Double longitud, Double radioKm) {

        return repartidorRepository
                .findRepartidoresDisponiblesCercanos(latitud, longitud, radioKm)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public RepartidorEstadisticasDTO getEstadisticas(Long repartidorId)
            throws NotFoundException {

        Usuario repartidor = getRepartidorById(repartidorId);

        Long totalEntregas = repartidorRepository.countEntregasCompletadas(repartidorId);
        Double totalGanadoDouble = repartidorRepository.calcularTotalGanado(repartidorId);
        BigDecimal totalGanado = BigDecimal.valueOf(totalGanadoDouble != null ? totalGanadoDouble : 0.0);

        // TODO: Implementar cálculo de calificación promedio cuando se agreguen calificaciones
        BigDecimal promedioCalificacion = BigDecimal.ZERO;

        // TODO: Implementar conteo por períodos (hoy, semana, mes)
        Long entregasHoy = 0L;
        Long entregasEstaSemana = 0L;
        Long entregasEsteMes = 0L;

        return new RepartidorEstadisticasDTO(
                totalEntregas,
                totalGanado,
                promedioCalificacion,
                entregasHoy,
                entregasEstaSemana,
                entregasEsteMes
        );
    }

    // ===== MÉTODOS PRIVADOS =====

    private Usuario getRepartidorById(Long id) throws NotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Repartidor no encontrado"));

        if (!usuario.isRepartidor()) {
            throw new NotFoundException("El usuario no es un repartidor");
        }

        return usuario;
    }

    private RepartidorResponseDTO mapToResponseDTO(Usuario repartidor) {
        return new RepartidorResponseDTO(
                repartidor.getId(),
                repartidor.getNombre(),
                repartidor.getApellido(),
                repartidor.getTelefono(),
                repartidor.getEmail(),
                repartidor.getLatitudActual(),
                repartidor.getLongitudActual()
        );
    }
}