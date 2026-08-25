package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.enums.AlcancePromocion;
import utn.back.mordiscoapi.model.enums.TipoDescuento;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocionServiceImplTest {
    private static final Long RESTAURANT_ID = 10L;
    private static final Long OTHER_RESTAURANT_ID = 20L;
    private static final Long PROMOTION_ID = 30L;
    private static final List<Long> PRODUCT_IDS = List.of(40L, 50L);

    @Mock private PromocionRepository repository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private ProductoRepository productoRepository;

    private PromocionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PromocionServiceImpl(repository, restauranteRepository, productoRepository);
    }

    @Test
    void createsSpecificPromotionWithProductsFromItsRestaurantInOneQuery() throws Exception {
        List<Producto> products = List.of(product(40L, "10.00"), product(50L, "20.00"));
        when(productoRepository.findAllByIdInAndMenuRestauranteId(PRODUCT_IDS, RESTAURANT_ID)).thenReturn(products);

        service.save(specificPromotion(TipoDescuento.PORCENTAJE, PRODUCT_IDS));

        ArgumentCaptor<Promocion> promotion = ArgumentCaptor.forClass(Promocion.class);
        verify(repository).save(promotion.capture());
        assertSame(products, promotion.getValue().getProductosAplicables());
        verify(productoRepository, times(1)).findAllByIdInAndMenuRestauranteId(PRODUCT_IDS, RESTAURANT_ID);
    }

    @Test
    void rejectsMissingOrForeignSpecificProductsWithoutSaving() {
        when(productoRepository.findAllByIdInAndMenuRestauranteId(PRODUCT_IDS, RESTAURANT_ID))
                .thenReturn(List.of(product(40L, "10.00")));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.save(specificPromotion(TipoDescuento.PORCENTAJE, PRODUCT_IDS)));

        assertEquals("Los productos especificados no son válidos para el restaurante", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsDuplicateSpecificProductIdsWithoutSaving() {
        List<Long> duplicateProductIds = List.of(40L, 40L);
        when(productoRepository.findAllByIdInAndMenuRestauranteId(duplicateProductIds, RESTAURANT_ID))
                .thenReturn(List.of(product(40L, "10.00")));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.save(specificPromotion(TipoDescuento.PORCENTAJE, duplicateProductIds)));

        assertEquals("Los productos especificados no son válidos para el restaurante", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updatesOwnPromotionAfterValidatingSpecificProductsAndFixedAmountInOneQuery() throws Exception {
        Promocion promotion = existingPromotion(RESTAURANT_ID, "Original");
        Restaurante restaurant = Restaurante.builder().id(RESTAURANT_ID).build();
        List<Producto> products = List.of(product(40L, "10.00"), product(50L, "20.00"));
        when(repository.findById(PROMOTION_ID)).thenReturn(Optional.of(promotion));
        when(restauranteRepository.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        when(productoRepository.findAllByIdInAndMenuRestauranteId(PRODUCT_IDS, RESTAURANT_ID)).thenReturn(products);

        service.update(PROMOTION_ID, specificPromotion(TipoDescuento.MONTO_FIJO, PRODUCT_IDS));

        assertEquals("Promotion", promotion.getDescripcion());
        assertSame(products, promotion.getProductosAplicables());
        verify(repository).save(promotion);
        verify(productoRepository, times(1)).findAllByIdInAndMenuRestauranteId(PRODUCT_IDS, RESTAURANT_ID);
    }

    @Test
    void rejectsReassociationToAnotherRestaurantWithoutPartialMutationOrSaving() {
        Promocion promotion = existingPromotion(OTHER_RESTAURANT_ID, "Original");
        List<Producto> originalProducts = promotion.getProductosAplicables();
        when(repository.findById(PROMOTION_ID)).thenReturn(Optional.of(promotion));
        when(restauranteRepository.findById(RESTAURANT_ID))
                .thenReturn(Optional.of(Restaurante.builder().id(RESTAURANT_ID).build()));

        assertThrows(BadRequestException.class,
                () -> service.update(PROMOTION_ID, specificPromotion(TipoDescuento.PORCENTAJE, PRODUCT_IDS)));

        assertEquals("Original", promotion.getDescripcion());
        assertSame(originalProducts, promotion.getProductosAplicables());
        verify(repository, never()).save(any());
        verify(productoRepository, never()).findAllByIdInAndMenuRestauranteId(eq(PRODUCT_IDS), eq(RESTAURANT_ID));
    }

    private PromocionRequestDTO specificPromotion(TipoDescuento type, List<Long> productIds) {
        return new PromocionRequestDTO("Promotion", 5.0, type, AlcancePromocion.PRODUCTOS_ESPECIFICOS,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), true, productIds, RESTAURANT_ID);
    }

    private Promocion existingPromotion(Long restaurantId, String description) {
        return Promocion.builder()
                .descripcion(description)
                .restaurante(Restaurante.builder().id(restaurantId).build())
                .fechaInicio(LocalDate.now().plusDays(1))
                .productosAplicables(new ArrayList<>(List.of(product(99L, "15.00"))))
                .build();
    }

    private Producto product(Long id, String price) {
        return Producto.builder().id(id).precio(new BigDecimal(price)).build();
    }
}
