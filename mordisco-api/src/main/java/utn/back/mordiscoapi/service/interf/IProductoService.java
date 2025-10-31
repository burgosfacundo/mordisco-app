package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.projection.ProductoProjection;

public interface IProductoService {
    Page<ProductoProjection> findAll(int pageNo,int pageSize);
    ProductoProjection findById(Long id) throws NotFoundException;
}
