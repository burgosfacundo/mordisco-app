package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

@Component("horarioSecurity")
@RequiredArgsConstructor
public class HorarioSecurity {
    private final AuthUtils authUtils;
    private final HorarioRepository horarioRepository;

    public boolean puedeAccederAHorario(Long idHorario) {
        return authUtils.getUsuarioAutenticado()
                .filter(usuario -> usuario.getId() != null)
                .map(usuario -> horarioRepository.existsByIdAndRestaurante_Usuario_Id(idHorario, usuario.getId()))
                .orElse(false);
    }
}
