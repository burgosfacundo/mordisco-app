package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseCardDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoUpdateDTO;

public interface IProductoService {
    Page<ProductoResponseCardDTO> findAllByIdMenu(int pageNo, int pageSize,Long idMenu);
    ProductoResponseDTO findById(Long id) throws NotFoundException;
    void save(ProductoRequestDTO dto) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void update(Long id, ProductoUpdateDTO dto) throws NotFoundException;
}
