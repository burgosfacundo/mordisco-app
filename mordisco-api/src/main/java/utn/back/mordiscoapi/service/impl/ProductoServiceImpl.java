package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.projection.ProductoProjection;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.service.interf.IProductoService;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository repository;

    @Override
    public Page<ProductoProjection> findAll(int pageNo,int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findAllComplete(pageable);
    }

    @Override
    public ProductoProjection findById(Long id) throws NotFoundException {
        return repository.findCompleteById(id).orElseThrow(
                () -> new NotFoundException("Producto no encontrado")
        );
    }
}
