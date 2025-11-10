package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.CalificacionRestauranteMapper;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.entity.CalificacionRestaurante;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;
import utn.back.mordiscoapi.repository.CalificacionRestauranteRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.ICalificacionRestaurante;


@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionRestauranteImpl implements ICalificacionRestaurante {
    private final CalificacionRestauranteRepository repository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * Guarda una calificación de restaurante.
     * @param dto DTO de la calificación a guardar.
     * @throws NotFoundException si el restaurante o el usuario no existen.
     */
    @Override
    public void save(CalificacionRestauranteDTO dto) throws NotFoundException, BadRequestException {
        if (!restauranteRepository.existsById(dto.restauranteId())) {
            throw new NotFoundException("El id del restaurante no existe");
        }

        if (!usuarioRepository.existsById(dto.calificacionDTO().idUsuario())) {
            throw new NotFoundException("El id del usuario no existe");
        }

        boolean hizoPedido = pedidoRepository.existsByCliente_IdAndRestaurante_Id(
                dto.calificacionDTO().idUsuario(),
                dto.restauranteId()
        );

        if (!hizoPedido) {
            throw new BadRequestException("Solo puedes calificar un restaurante si ya realizaste un pedido allí.");
        }

        CalificacionRestaurante calificacionRestaurante = CalificacionRestauranteMapper.toCalificacionRestaurante(dto);
        repository.save(calificacionRestaurante);
    }


    /**
     * Obtiene todas las calificaciones de restaurantes.
     * @return una lista de calificaciones de restaurantes.
     */
    @Override
    public Page<CalificacionRestauranteProjection> findAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return  repository.findAllProjection(pageable);
    }

    /**
     * Elimina una calificación por ID
     * @param aLong id de la calificación que se desea borrar
     * @throws NotFoundException si no encuentra la calificación que se desea borrar
     */
    @Override
    public void delete(Long aLong) throws NotFoundException {
        if (!repository.existsById(aLong)) {
            throw new NotFoundException("No se encontro la claificacion que se desea borrar");
        }
        repository.deleteById(aLong);
    }

    @Override
    public Page<CalificacionRestauranteProjection> findAllByIdRestaurante(int page, int size, Long idRestaurante) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByRestaurante_Id(idRestaurante,pageable);
    }
}
