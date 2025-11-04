package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.HorarioAtencionMapper;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IHorarioService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HorariosServiceImpl implements IHorarioService {
    private final HorarioRepository repository;
    private final RestauranteRepository restauranteRepository;

    @Override
    public void save(Long idRestaurante, HorarioAtencionRequestDTO horario) throws NotFoundException {
        var restaurante = restauranteRepository.findById(idRestaurante).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado")
        );

        repository.save(HorarioAtencionMapper.toEntity(restaurante,horario));
    }

    @Override
    public Page<HorarioAtencionResponseDTO> findAllByIdRestaurante(int page,int size,Long idRestaurante) throws NotFoundException {
        if(!restauranteRepository.existsById(idRestaurante)) throw new NotFoundException("Restaurante no encontrado");

        Pageable pageable = PageRequest.of(page, size);

        var horarios = repository.findAllByRestauranteId(idRestaurante,pageable);

        return horarios.map(HorarioAtencionMapper::toDTO);
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
