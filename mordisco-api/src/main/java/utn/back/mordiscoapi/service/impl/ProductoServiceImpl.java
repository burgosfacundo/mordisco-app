package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.ImagenMapper;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.mapper.ProductoMapper;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
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

    /**
     * 🆕 VAL-002: Elimina un producto validando que no tenga pedidos activos
     *
     * @param id del producto a eliminar
     * @throws NotFoundException si no se encuentra el producto
     * @throws BadRequestException si el producto está en pedidos activos
     */
    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException, BadRequestException {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Producto no encontrado");
        }

        long pedidosActivos = repository.countPedidosActivosByProducto(id);

        if (pedidosActivos > 0) {
            String mensaje = String.format(
                    "No se puede eliminar el producto. Está incluido en %d pedido%s activo%s. " +
                            "Debe esperar a que se completen o cancelarlos antes de eliminarlo.",
                    pedidosActivos,
                    pedidosActivos == 1 ? "" : "s",
                    pedidosActivos == 1 ? "" : "s"
            );

            throw new BadRequestException(mensaje);
        }

        repository.deleteById(id);
    }

    /**
     * 🆕 Obtiene los pedidos activos que contienen un producto
     */
    @Override
    public Page<PedidoResponseDTO> getPedidosActivosByProducto(Long productoId, int page, int size)
            throws NotFoundException {
        if (!repository.existsById(productoId)) {
            throw new NotFoundException("Producto no encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);
        return repository.findPedidosActivosByProducto(productoId, pageable)
                .map(PedidoMapper::toDTO);
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
