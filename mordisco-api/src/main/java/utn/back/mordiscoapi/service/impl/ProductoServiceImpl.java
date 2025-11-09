package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.ImagenMapper;
import utn.back.mordiscoapi.mapper.ProductoMapper;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseCardDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoUpdateDTO;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.service.interf.IProductoService;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository repository;
    private final MenuRepository menuRepository;


    @Override
    public Page<ProductoResponseCardDTO> findAllByIdMenu(int pageNo, int pageSize,Long idMenu) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findAllByIdMenu(pageable,idMenu).map(ProductoMapper::toDtoCard);
    }

    @Override
    public ProductoResponseDTO findById(Long id) throws NotFoundException {
        var producto = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Producto no encontrado")
        );


        return ProductoMapper.toDto(producto);
    }

    @Override
    public void save(ProductoRequestDTO dto) throws NotFoundException {
        var menu = menuRepository.findById(dto.idMenu()).orElseThrow(
                () -> new NotFoundException("Menu no encontrado")
        );

        repository.save(ProductoMapper.toEntity(menu, dto));
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        if (!repository.existsById(id)) throw new NotFoundException("Producto no encontrado");
        repository.deleteById(id);
    }

    @Override
    public void update(Long id, ProductoUpdateDTO dto) throws NotFoundException {
        var producto = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Producto no encontrado")
        );
        ImagenMapper.applyUpdate(dto.imagen(),producto.getImagen());

        ProductoMapper.applyUpdate(producto, dto);
        repository.save(producto);
    }
}
