package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.model.dto.DireccionDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.repository.DireccionRepository;
import utn.back.mordiscoapi.service.DireccionService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DireccionServiceImpl implements DireccionRepository {

    private final DireccionRepository repository;

    @Override
    public void save(DireccionDTO direccionDTO) throws BadRequestException {
        
    }
}
