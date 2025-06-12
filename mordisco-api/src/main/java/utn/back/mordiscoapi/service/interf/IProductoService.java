package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.projection.ProductoProjection;

import java.util.List;

public interface IProductoService {
    List<ProductoProjection> findAll();
    ProductoProjection findById(Long id) throws NotFoundException;
}
