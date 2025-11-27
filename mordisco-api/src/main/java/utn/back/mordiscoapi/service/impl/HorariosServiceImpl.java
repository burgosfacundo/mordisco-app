package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.common.util.HorarioValidator;
import utn.back.mordiscoapi.mapper.HorarioAtencionMapper;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IHorarioService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HorariosServiceImpl implements IHorarioService {
    private final HorarioRepository repository;
    private final RestauranteRepository restauranteRepository;

    @Override
    public Long save(Long idRestaurante, HorarioAtencionRequestDTO horario) throws NotFoundException, BadRequestException {
        var restaurante = restauranteRepository.findById(idRestaurante).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado")
        );

        List<HorarioAtencion> horariosExistentes = repository.findAllByRestauranteId(idRestaurante);

        // Validar que no haya solapamiento
        try {
            HorarioValidator.validarNoSolapamiento(
                    horario.dia(),
                    horario.horaApertura(),
                    horario.horaCierre(),
                    horariosExistentes,
                    null // null porque es creación
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        HorarioAtencion h = HorarioAtencionMapper.toEntity(restaurante, horario);
        repository.save(h);

        return h.getId();
    }

    @Override
    public List<HorarioAtencionResponseDTO> findAllByIdRestaurante(Long idRestaurante) throws NotFoundException {
        if(!restauranteRepository.existsById(idRestaurante)) throw new NotFoundException("Restaurante no encontrado");
        var horarios = repository.findAllByRestauranteId(idRestaurante);
        return horarios.stream().map(HorarioAtencionMapper::toDTO).toList();
    }

    @Override
    public void update(Long idHorario, HorarioAtencionRequestDTO dto) throws NotFoundException, BadRequestException {
        var horario = repository.findById(idHorario).orElseThrow(
                () -> new NotFoundException("Horario no encontrado")
        );

        Long restauranteId = horario.getRestaurante().getId();

        // Obtener todos los horarios del restaurante
        List<HorarioAtencion> horariosExistentes = repository.findAllByRestauranteId(restauranteId);

        // Validar solapamiento (excluyendo el horario actual)
        try {
            HorarioValidator.validarNoSolapamiento(
                    dto.dia(),
                    dto.horaApertura(),
                    dto.horaCierre(),
                    horariosExistentes,
                    idHorario // excluir el horario que se está actualizando
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        // Si pasa la validación, actualizar
        HorarioAtencionMapper.applyUpdate(dto, horario);
        repository.save(horario);
    }

    @Override
    public void delete(Long idHorario) throws NotFoundException {
        if(!repository.existsById(idHorario)) throw new NotFoundException("Horario no encontrado");
        repository.deleteById(idHorario);
    }
}
