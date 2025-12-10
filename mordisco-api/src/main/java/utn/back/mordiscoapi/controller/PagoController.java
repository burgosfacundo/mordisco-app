package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.pago.PagoResponse;
import utn.back.mordiscoapi.service.PagoService;

@Tag(name = "Pagos")
@RestController
@RequestMapping("/api/pedidos/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService pagoService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR') or @pedidoSecurity.esPropietarioPedido(#idPedido) or @pedidoSecurity.esPropietarioRestaurantePedido(#idPedido)")
    @GetMapping("/{idPedido}")
    public ResponseEntity<PagoResponse> getPagoByPedidoId(@PathVariable Long idPedido)
            throws NotFoundException {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(idPedido));
    }
}
