package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PromocionMapper;
import utn.back.mordiscoapi.model.dto.promocion.PromocionDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IPromocionService;

import java.time.LocalDate;
import java.util.List;

@Slf4j // Anotación de Lombok para el registro de logs
@Service // Anotación de servicio de Spring para indicar que esta clase es un servicio
@RequiredArgsConstructor // Anotación de lombok para generar un constructor con los campos finales
public class PromocionServiceImpl implements IPromocionService {
    // Inyección de dependencias de PromocionRepository a través del constructor de lombok @RequiredArgsConstructor
    private final PromocionRepository repository;
    private final RestauranteRepository restauranteRepository;

    /**
     * Guarda una promoción.
     * @param dto DTO de la promoción a guardar.
     */
    @Override
    public void save(PromocionDTO dto) throws BadRequestException {
        if (dto.fechaInicio().isBefore(LocalDate.now())) {
            // Si la fecha de inicio es anterior a la fecha actual, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        if (dto.fechaInicio().isAfter(dto.fechaFin())) {
            // Si la fecha de inicio es posterior a la fecha de fin, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        try {
            // Mapeo la DTO a la entidad
            Promocion promocion = PromocionMapper.toEntity(dto);
            // Guardar la entidad en la base de datos
            repository.save(promocion);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            String mensaje = "Error al guardar la promoción";
            if (e.getMessage() != null && e.getMessage().contains("restaurante_id")) {
                mensaje = "El restaurante asociado no existe o es inválido";
            } else if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                mensaje = "Ya existe una promoción con los mismos datos únicos";
            }
            throw new BadRequestException(mensaje);
        }
    }

    /**
     * Obtiene todas las promociones paginadas.
     * @return una página de promociones proyectadas.
     */
    @Override
    public List<PromocionProjection> findAll() {
        // Obtener todas las promociones usando el findAllProject creado en el repositorio
        return repository.findAllProject();
    }


    /**
     * Obtiene una promoción por su ID.
     * @param id el ID de la promoción a buscar.
     * @return la promoción proyectada.
     * @throws NotFoundException si la promoción no se encuentra.
     */
    @Override
    public PromocionProjection findById(Long id) throws NotFoundException {
        // Manejo de Optional
        // Obtenemos Optional<PromocionProjection> usando el findProjectById creado en el repositorio
        // Si no existe, lanzamos una excepción NotFoundException
        // Si existe, devolvemos la promoción
        return repository.findProjectById(id).orElseThrow(
                () -> new NotFoundException("Promoción no encontrada")
        );
    }


    /**
     * Actualiza una promoción.
     * @param id el ID de la promoción a actualizar.
     * @param dto DTO de la promoción a actualizar.
     * @throws NotFoundException si la promoción no se encuentra.
     * @throws BadRequestException si hay un error al actualizar la promoción.
     */
    @Override
    public void update(Long id, PromocionDTO dto) throws NotFoundException,BadRequestException {
        // Manejo de Optional
        // Obtenemos Optional<Promocion> usando el findById por defecto
        // Si no existe, lanzamos una excepción NotFoundException
        // Si existe, usamos la promoción para modificarla
        Promocion promocion = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Promoción no encontrada")
        );

        if (dto.fechaInicio().isBefore(LocalDate.now())) {
            // Si la fecha de inicio es anterior a la fecha actual, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        if (dto.fechaInicio().isAfter(dto.fechaFin())) {
            // Si la fecha de inicio es posterior a la fecha de fin, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Verificamos si el restaurante existe usando el findById por defecto
        // Si no existe, lanzamos una excepción BadRequestException
        var restaurante = restauranteRepository.findById(dto.restauranteId())
                .orElseThrow(() -> new BadRequestException("El restaurante asociado no existe"));

        // Actualizar los campos de la promoción
        promocion.setDescripcion(dto.descripcion());
        promocion.setDescuento(dto.descuento());
        promocion.setFechaInicio(dto.fechaInicio());
        promocion.setFechaFin(dto.fechaFin());
        promocion.setRestaurante(restaurante);

        // Guardamos la promoción actualizada
        repository.save(promocion);
    }


    /**
     * Elimina una promoción por su ID.
     * @param id el ID de la promoción a eliminar.
     * @throws NotFoundException si la promoción no se encuentra.
     */
    @Override
    public void delete(Long id) throws NotFoundException {
        //Verificamos si existe la promoción usando el existsById por defecto
        // Si no existe, lanzamos una excepción NotFoundException
        if (!repository.existsById(id)) {
            throw new NotFoundException("Promoción no encontrada");
        }

        // Si existe, eliminamos la promoción usando el deleteById por defecto
        repository.deleteById(id);
    }

    /**
     * Lista las promociones por restaurante.
     * @param idRestaurante el ID del restaurante para listar sus promociones.
     * @return una lista de DTOs de promociones.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Override
    public List<PromocionResponseDTO> listarPromoPorRestaurante (Long idRestaurante)throws NotFoundException {
        if(repository.findByRestauranteId(idRestaurante).isEmpty()){
            throw new NotFoundException("No se encontro el restaurante");
        }
        List<Promocion> lista = repository.findByRestauranteId(idRestaurante);
        return lista.stream().map(PromocionMapper::toDTO).toList();
    }
}
