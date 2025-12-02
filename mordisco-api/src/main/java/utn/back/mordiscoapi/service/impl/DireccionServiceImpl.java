package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.DireccionMapper;
import utn.back.mordiscoapi.model.dto.direccion.DireccionRequestDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.DireccionRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.service.GeocodingService;
import utn.back.mordiscoapi.service.interf.IDireccionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl implements IDireccionService {
    private final AuthUtils authUtils;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final GeocodingService geocodingService;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    @Override
    public List<DireccionResponseDTO> getMisDirecciones()
            throws BadRequestException {
        Usuario usuario = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        return direccionRepository.findAllByUsuarioId(usuario.getId())
                .stream()
                .map(DireccionMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public DireccionResponseDTO createMiDireccion(DireccionRequestDTO dto)
            throws BadRequestException {
        Usuario u = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        // Validar que no exista una dirección duplicada
        boolean existeDuplicada = direccionRepository.findAllByUsuarioId(u.getId())
                .stream()
                .anyMatch(d ->
                        d.getCalle().equalsIgnoreCase(dto.calle()) &&
                        d.getNumero().equals(dto.numero()) &&
                        java.util.Objects.equals(d.getPiso(), dto.piso()) &&
                        java.util.Objects.equals(d.getDepto(), dto.depto()) &&
                        d.getCiudad().equalsIgnoreCase(dto.ciudad()) &&
                        d.getCodigoPostal().equals(dto.codigoPostal())
                );

        if (existeDuplicada) {
            StringBuilder mensaje = new StringBuilder("Ya tienes una dirección registrada en ");
            mensaje.append(dto.calle()).append(" ").append(dto.numero());
            if (dto.piso() != null && !dto.piso().isEmpty()) {
                mensaje.append(", Piso ").append(dto.piso());
                if (dto.depto() != null && !dto.depto().isEmpty()) {
                    mensaje.append(" ").append(dto.depto());
                }
            }
            mensaje.append(", ").append(dto.ciudad());
            throw new BadRequestException(mensaje.toString());
        }

        Direccion d = DireccionMapper.fromCreateDTO(dto);
        d.setUsuario(u);

        geocodingService.geocode(d.getCalle(), d.getNumero(), d.getCiudad(), d.getCodigoPostal())
                .ifPresent(latLng -> {
                    d.setLatitud(latLng.lat());
                    d.setLongitud(latLng.lng());
                });

        if (d.getLatitud() == null || d.getLongitud() == null) {
            throw new BadRequestException("La dirección es incorrecta");
        }


        direccionRepository.save(d);
        return DireccionMapper.toDTO(d);
    }


    @Override
    @Transactional
        public DireccionResponseDTO updateMiDireccion(Long id,DireccionRequestDTO dto)
            throws NotFoundException, BadRequestException {
        Usuario usuario = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        Direccion d = direccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dirección no encontrada"));

        if (!d.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("No tienes permiso para modificar esta dirección");
        }

        // Validar que no exista una dirección duplicada (excluyendo la actual)
        boolean existeDuplicada = direccionRepository.findAllByUsuarioId(usuario.getId())
                .stream()
                .filter(dir -> !dir.getId().equals(id)) // Excluir la dirección actual
                .anyMatch(dir ->
                        dir.getCalle().equalsIgnoreCase(dto.calle()) &&
                        dir.getNumero().equals(dto.numero()) &&
                        java.util.Objects.equals(dir.getPiso(), dto.piso()) &&
                        java.util.Objects.equals(dir.getDepto(), dto.depto()) &&
                        dir.getCiudad().equalsIgnoreCase(dto.ciudad()) &&
                        dir.getCodigoPostal().equals(dto.codigoPostal())
                );

        if (existeDuplicada) {
            StringBuilder mensaje = new StringBuilder("Ya tienes otra dirección registrada en ");
            mensaje.append(dto.calle()).append(" ").append(dto.numero());
            if (dto.piso() != null && !dto.piso().isEmpty()) {
                mensaje.append(", Piso ").append(dto.piso());
                if (dto.depto() != null && !dto.depto().isEmpty()) {
                    mensaje.append(" ").append(dto.depto());
                }
            }
            mensaje.append(", ").append(dto.ciudad());
            throw new BadRequestException(mensaje.toString());
        }

        geocodingService.geocode(d.getCalle(), d.getNumero(), d.getCiudad(), d.getCodigoPostal())
                .ifPresent(latLng -> {
                    d.setLatitud(latLng.lat());
                    d.setLongitud(latLng.lng());
                });

        if (d.getLatitud() == null || d.getLongitud() == null) {
            throw new BadRequestException("La dirección es incorrecta");
        }

        DireccionMapper.applyUpdate(dto, d);
        direccionRepository.save(d);
        return DireccionMapper.toDTO(d);

    }

    @Override
    @Transactional
    public void deleteMiDireccion(Long id) throws NotFoundException, BadRequestException {

        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dirección no encontrada"));

        pedidoRepository.desasociarDireccion(id);

        direccionRepository.delete(direccion);
    }

    @Override
    public List<DireccionResponseDTO> getByUsuarioId(Long userId) throws NotFoundException {
        if (!usuarioRepository.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado");
        }

        return direccionRepository.findAllByUsuarioId(userId)
                .stream()
                .map(DireccionMapper::toDTO)
                .toList();
    }
}

