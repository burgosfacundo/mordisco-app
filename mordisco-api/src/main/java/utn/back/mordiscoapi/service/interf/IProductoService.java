package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.projection.ProductoProjection;

import java.util.List;

public interface IProductoService {
    void save(ProductoRequestDTO productoRequestDTO) throws BadRequestException;
    List<ProductoProjection> findAll();
    ProductoProjection findById(Long id) throws NotFoundException;
    void delete(Long aLong) throws NotFoundException;
}
