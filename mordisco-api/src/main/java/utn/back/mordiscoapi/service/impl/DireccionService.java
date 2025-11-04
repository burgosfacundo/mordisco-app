package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.DireccionMapper;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.DireccionRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IDireccionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionService implements IDireccionService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final GeocodingService geocodingService;

    @Transactional(readOnly = true)
    @Override
    public List<DireccionResponseDTO> list(Long usuarioId) {
        return direccionRepository.findAllByUsuarioId(usuarioId).stream()
                .map(DireccionMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public Long add(Long usuarioId, DireccionCreateDTO dto) throws NotFoundException {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Direccion d = DireccionMapper.fromCreateDTO(dto);
        d.setUsuario(u);

        geocodingService.geocode(d.getCalle(), d.getNumero(), d.getCiudad(), d.getCodigoPostal())
                .ifPresent(latLng -> {
                    d.setLatitud(latLng.lat());
                    d.setLongitud(latLng.lng());
                });


        direccionRepository.save(d);
        return d.getId();
    }

    @Transactional
    @Override
    public void update(Long usuarioId, DireccionUpdateDTO dto) throws NotFoundException {
        Direccion d = direccionRepository.findByIdAndUsuarioId(dto.id(), usuarioId)
                .orElseThrow(() -> new NotFoundException("Dirección no encontrada o no pertenece al usuario"));

        DireccionMapper.applyUpdate(dto, d);


        geocodingService.geocode(d.getCalle(), d.getNumero(), d.getCiudad(), d.getCodigoPostal())
                .ifPresent(latLng -> {
                    d.setLatitud(latLng.lat());
                    d.setLongitud(latLng.lng());
                });

    }

    @Transactional
    @Override
    public void delete(Long usuarioId, Long dirId) throws NotFoundException {
        Direccion d = direccionRepository.findByIdAndUsuarioId(dirId, usuarioId)
                .orElseThrow(() -> new NotFoundException("Dirección no encontrada o no pertenece al usuario"));
        direccionRepository.delete(d);
    }
}

