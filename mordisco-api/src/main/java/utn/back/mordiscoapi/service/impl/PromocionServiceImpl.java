package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PromocionMapper;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IPromocionService;

import java.time.LocalDate;

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
    public void save(PromocionRequestDTO dto) throws BadRequestException {
        if (dto.fechaInicio().isBefore(LocalDate.now())) {
            // Si la fecha de inicio es anterior a la fecha actual, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        if (dto.fechaInicio().isAfter(dto.fechaFin())) {
            // Si la fecha de inicio es posterior a la fecha de fin, lanzamos una excepción BadRequestException
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Mapeo la DTO a la entidad
        Promocion promocion = PromocionMapper.toEntity(dto);
        // Guardar la entidad en la base de datos
        repository.save(promocion);
    }

    /**
     * Obtiene todas las promociones paginadas.
     * @return una página de promociones proyectadas.
     */
    @Override
    public Page<PromocionProjection> findAll(int pageNo,int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findAllProject(pageable);
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
    public void update(Long id, PromocionRequestDTO dto) throws NotFoundException,BadRequestException {
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

        // Verificamos si el restaurante es el mismo que el de la promoción actual
        if (!promocion.getRestaurante().getId().equals(dto.restauranteId())) {
            throw new BadRequestException("No se puede actualizar la promoción a un restaurante diferente");
        }

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
     * @param idPromocion el ID de la promoción a eliminar.
     * @param idRestaurante el ID del restaurante al que pertenece la promoción.
     * @throws NotFoundException si la promoción no se encuentra.
     */
    @Override
    public void delete(Long idRestaurante,Long idPromocion) throws NotFoundException {
        var promocion = repository.findById(idPromocion)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada"));

        // Verificamos si el restaurante existe usando el findById por defecto
        // Si no existe, lanzamos una excepción NotFoundException
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new NotFoundException("Restaurante no encontrado");
        }
        // Verificamos si el restaurante es el mismo que el de la promoción actual
        if (!promocion.getRestaurante().getId().equals(idRestaurante)) {
            throw new NotFoundException("No se puede eliminar la promoción de un restaurante diferente");
        }

        // Si existe, eliminamos la promoción usando el deleteById por defecto
        repository.deleteById(idPromocion);
    }

    /**
     * Lista las promociones por restaurante.
     * @param idRestaurante el ID del restaurante para listar sus promociones.
     * @return una lista de DTOs de promociones.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Override
    public Page<PromocionResponseDTO> listarPromoPorRestaurante (int pageNo,int pageSize,Long idRestaurante) throws NotFoundException {
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new NotFoundException("Restaurante no encontrado");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findByRestauranteId(pageable,idRestaurante)
                .map(PromocionMapper::toDTO);
    }
}
