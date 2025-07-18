package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.projection.ProductoProjection;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.service.interf.IProductoService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository repository;

    @Override
    public List<ProductoProjection> findAll() {
        return repository.findAllComplete();
    }

    @Override
    public ProductoProjection findById(Long id) throws NotFoundException {
        return repository.findCompleteById(id).orElseThrow(
                () -> new NotFoundException("Producto no encontrado")
        );
    }
}
