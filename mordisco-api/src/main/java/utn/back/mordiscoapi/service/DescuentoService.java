package utn.back.mordiscoapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.enums.AlcancePromocion;
import utn.back.mordiscoapi.model.enums.TipoDescuento;
import utn.back.mordiscoapi.repository.PromocionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para calcular descuentos automáticos en productos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DescuentoService {

    private final PromocionRepository promocionRepository;

    /**
     * Calcula el precio final de un producto considerando promociones activas
     * 
     * @param producto Producto a evaluar
     * @param restaurante Restaurante del producto
     * @param fecha Fecha para validar vigencia de promociones
     * @return Precio con descuento aplicado (o precio original si no hay promociones)
     */
    public BigDecimal calcularPrecioConDescuento(
            Producto producto,
            Restaurante restaurante,
            LocalDate fecha
    ) {
        // 1. Buscar promociones activas del restaurante
        List<Promocion> promocionesActivas = promocionRepository
                .findPromocionesActivasByRestauranteAndFecha(
                        restaurante.getId(),
                        fecha
                );

        if (promocionesActivas.isEmpty()) {
            return producto.getPrecio();
        }

        // 2. Filtrar promociones aplicables al producto
        List<Promocion> promocionesAplicables = promocionesActivas.stream()
                .filter(promo -> esAplicableAProducto(promo, producto))
                .toList();

        if (promocionesAplicables.isEmpty()) {
            return producto.getPrecio();
        }

        // 3. Obtener la mejor promoción (mayor descuento)
        Optional<Promocion> mejorPromocion = promocionesAplicables.stream()
                .max(Comparator.comparing(promo -> 
                    calcularMontoDescuento(producto.getPrecio(), promo)
                ));

        // 4. Aplicar el mejor descuento
        if (mejorPromocion.isPresent()) {
            BigDecimal precioConDescuento = aplicarDescuento(
                    producto.getPrecio(),
                    mejorPromocion.get()
            );
            
            log.debug("Producto {}: Precio original ${}, Descuento aplicado: {}%, Precio final ${}",
                    producto.getNombre(),
                    producto.getPrecio(),
                    mejorPromocion.get().getDescuento(),
                    precioConDescuento
            );
            
            return precioConDescuento;
        }

        return producto.getPrecio();
    }

    /**
     * Obtiene la mejor promoción aplicable a un producto
     * 
     * @param producto Producto a evaluar
     * @param restaurante Restaurante del producto
     * @param fecha Fecha para validar vigencia
     * @return Promoción aplicable o null si no hay
     */
    public Promocion obtenerMejorPromocion(
            Producto producto,
            Restaurante restaurante,
            LocalDate fecha
    ) {
        List<Promocion> promocionesActivas = promocionRepository
                .findPromocionesActivasByRestauranteAndFecha(
                        restaurante.getId(),
                        fecha
                );

        return promocionesActivas.stream()
                .filter(promo -> esAplicableAProducto(promo, producto))
                .max(Comparator.comparing(promo -> 
                    calcularMontoDescuento(producto.getPrecio(), promo)
                ))
                .orElse(null);
    }

    /**
     * Calcula el porcentaje de descuento efectivo
     * 
     * @param precioOriginal Precio original del producto
     * @param promocion Promoción a aplicar
     * @return Porcentaje de descuento (0-100)
     */
    public Double calcularPorcentajeDescuento(BigDecimal precioOriginal, Promocion promocion) {
        if (promocion == null || precioOriginal.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        BigDecimal montoDescuento = calcularMontoDescuento(precioOriginal, promocion);
        
        return montoDescuento
                .divide(precioOriginal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Verifica si una promoción es aplicable a un producto específico
     */
    private boolean esAplicableAProducto(Promocion promocion, Producto producto) {
        // Si la promoción aplica a todo el menú
        if (promocion.getAlcance() == AlcancePromocion.TODO_MENU) {
            return true;
        }

        // Si es para productos específicos, verificar que el producto esté en la lista
        if (promocion.getAlcance() == AlcancePromocion.PRODUCTOS_ESPECIFICOS) {
            return promocion.getProductosAplicables() != null &&
                   promocion.getProductosAplicables().stream()
                           .anyMatch(p -> p.getId().equals(producto.getId()));
        }

        return false;
    }

    /**
     * Aplica el descuento al precio original
     */
    private BigDecimal aplicarDescuento(BigDecimal precioOriginal, Promocion promocion) {
        BigDecimal montoDescuento = calcularMontoDescuento(precioOriginal, promocion);
        BigDecimal precioFinal = precioOriginal.subtract(montoDescuento);
        
        // Asegurar que el precio nunca sea negativo
        return precioFinal.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el monto del descuento según el tipo
     */
    private BigDecimal calcularMontoDescuento(BigDecimal precioOriginal, Promocion promocion) {
        if (promocion.getTipoDescuento() == TipoDescuento.PORCENTAJE) {
            // Descuento porcentual: precio * (descuento / 100)
            return precioOriginal
                    .multiply(BigDecimal.valueOf(promocion.getDescuento()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Descuento fijo: monto directo
            BigDecimal descuentoFijo = BigDecimal.valueOf(promocion.getDescuento());
            // No puede descontar más que el precio original
            return descuentoFijo.min(precioOriginal);
        }
    }
}
