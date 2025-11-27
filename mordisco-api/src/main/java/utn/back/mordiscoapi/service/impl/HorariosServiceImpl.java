package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.HorarioAtencionMapper;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
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
    public Long save(Long idRestaurante, HorarioAtencionRequestDTO horario) throws NotFoundException {
        var restaurante = restauranteRepository.findById(idRestaurante).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado")
        );
        var hor = HorarioAtencionMapper.toEntity(restaurante,horario);
        repository.save(hor);
        return hor.getId();
    }

    @Override
    public List<HorarioAtencionResponseDTO> findAllByIdRestaurante(Long idRestaurante) throws NotFoundException {
        if(!restauranteRepository.existsById(idRestaurante)) throw new NotFoundException("Restaurante no encontrado");
        var horarios = repository.findAllByRestauranteId(idRestaurante);
        return horarios.stream().map(HorarioAtencionMapper::toDTO).toList();
    }

    @Override
    public void update(Long idHorario, HorarioAtencionRequestDTO dto) throws NotFoundException {
        var horario = repository.findById(idHorario).orElseThrow(
                () -> new NotFoundException("Horario no encontrado")
        );

        HorarioAtencionMapper.applyUpdate(dto, horario);
        repository.save(horario);
    }

    @Override
    public void delete(Long idHorario) throws NotFoundException {
        if(!repository.existsById(idHorario)) throw new NotFoundException("Horario no encontrado");
        repository.deleteById(idHorario);
    }
}
