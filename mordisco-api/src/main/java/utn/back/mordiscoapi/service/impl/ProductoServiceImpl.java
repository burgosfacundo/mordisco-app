package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.projection.ProductoProjection;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.service.CrudService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements CrudService<ProductoRequestDTO, ProductoProjection, Long> {

    private static ProductoRepository repository;

    @Override
    public void save(ProductoRequestDTO productoRequestDTO) throws BadRequestException {

    }

    @Override
    public List<ProductoProjection> findAll() {
        return List.of();
    }


    @Override
    public ProductoProjection findById(Long id) throws NotFoundException {
        return repository.findCompleteById(id).orElseThrow(
                () -> new NotFoundException("Producto no encontrado")
        );
    }

    @Override
    public void delete(Long aLong) throws NotFoundException {

    }
}
