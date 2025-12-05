import { Component, inject, OnInit, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';
import { DireccionService } from '../../../direccion/services/direccion-service';
import { CrearPedidoRequest } from '../../../../shared/models/pedido/crear-pedido-request';
import { CarritoService } from '../../../../shared/services/carrito/carrito-service';
import { MetodoPago } from '../../../../shared/models/enums/metodo-pago';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { ToastService } from '../../../../core/services/toast-service';
import { GeolocationService } from '../../../../shared/services/geolocation/geolocation-service';
import { ConfiguracionSistemaService } from '../../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';

@Component({
  selector: 'app-checkout-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatRadioModule,
    RouterLink
  ],
  templateUrl: './checkout-page.html',
  styleUrl: './checkout-page.css'
})
export class CheckoutPage implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private toastService = inject(ToastService);
  private carritoService = inject(CarritoService);
  private pedidoService = inject(PedidoService);
  private direccionService = inject(DireccionService);
  private authService = inject(AuthService);
  private geolocationService = inject(GeolocationService);
  private configuracionService = inject(ConfiguracionSistemaService);
  private restauranteService = inject(RestauranteService);

  checkoutForm!: FormGroup;
  direcciones = signal<DireccionResponse[]>([]);
  isLoading = signal(false);
  isProcessing = signal(false);
  costoDeliveryCalculado = signal<number>(0);
  direccionSeleccionadaId = signal<number | null>(null);
  montoMinimoPedido = signal<number>(0);

  // Signal para el tipo de entrega actual
  tipoEntregaActual = signal<TipoEntrega>(TipoEntrega.DELIVERY);

  // Computed del carrito
  items = this.carritoService.items;

  // Computed para saber si debe cobrar envío
  cobraEnvio = computed(() => 
    this.tipoEntregaActual() === TipoEntrega.DELIVERY
  );

  // Computed para el resumen con envío dinámico
  resumenConEnvio = computed(() => {
    const items = this.items();
    const subtotal = items.reduce((sum, item) => (
      sum + ((item.precioConDescuento ? item.precioConDescuento : item.precio) * item.cantidad)
    ), 0);
    
    const costoEnvio = this.cobraEnvio() ? this.costoDeliveryCalculado() : 0;
    const total = subtotal + costoEnvio;

    return {
      subtotal,
      costoEnvio,
      total,
      restauranteId: this.carritoService.resumen().restauranteId
    };
  });

  constructor() {
    // Effect para recalcular costo cuando cambia la dirección
    effect(() => {
      const direccionId = this.direccionSeleccionadaId();
      const tipoEntrega = this.tipoEntregaActual();
      
      if (tipoEntrega === TipoEntrega.DELIVERY && direccionId) {
        this.calcularCostoDelivery(direccionId);
      }
    });
  }

  ngOnInit(): void {
    if (!this.carritoService.tieneItems()) {
      this.toastService.success('El carrito está vacío');
      this.router.navigate(['/cliente/carrito']);
      return;
    }

    // Cargar monto mínimo del pedido
    this.configuracionService.getConfiguracionGeneral().subscribe({
      next: (cg) => this.montoMinimoPedido.set(cg.montoMinimoPedido),
      error: () => this.montoMinimoPedido.set(0)
    });

    this.initializeForm();
    this.cargarDirecciones();
  }

  private initializeForm(): void {
    this.checkoutForm = this.fb.group({
      tipoEntrega: [TipoEntrega.DELIVERY, Validators.required],
      direccionId: [null, Validators.required],
      metodoPago: [null, Validators.required],
      comentarios: ['']
    });

    this.checkoutForm.get('tipoEntrega')?.valueChanges.subscribe(tipo => {
      // Actualizar signal
      this.tipoEntregaActual.set(tipo);
      
      const direccionControl = this.checkoutForm.get('direccionId');
      
      if (tipo === TipoEntrega.DELIVERY) {
        direccionControl?.setValidators(Validators.required);
        
        if (this.direcciones().length > 0 && !direccionControl?.value) {
          direccionControl?.setValue(this.direcciones()[0].id);
        }
      } else {
        direccionControl?.clearValidators();
        direccionControl?.setValue(null);
        this.costoDeliveryCalculado.set(0);
      }
      
      direccionControl?.updateValueAndValidity();
    });

    // Escuchar cambios en la dirección seleccionada
    this.checkoutForm.get('direccionId')?.valueChanges.subscribe(direccionId => {
      this.direccionSeleccionadaId.set(direccionId);
    });
  }

  private cargarDirecciones(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.isLoading.set(true);

    this.direccionService.getMisDirecciones().subscribe({
      next: (direcciones) => {
        this.direcciones.set(direcciones);

        if (direcciones.length > 0 && this.tipoEntregaActual() === TipoEntrega.DELIVERY) {
          this.checkoutForm.patchValue({ direccionId: direcciones[0].id });
        }
        
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  confirmarPedido(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      
      if (this.tipoEntregaActual() === TipoEntrega.DELIVERY && !this.checkoutForm.get('direccionId')?.value) {
        this.toastService.error('Por favor selecciona una dirección de entrega');
      } else {
        this.toastService.error('Por favor completa todos los campos requeridos')
      }
      return;
    }

    this.isProcessing.set(true);

    const formValue = this.checkoutForm.value;
    const userId = this.authService.currentUser()?.userId;
    const resumen = this.resumenConEnvio();

    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    if (!resumen.restauranteId) {
      this.toastService.error('Error al procesar el pedido');
      this.isProcessing.set(false);
      return;
    }

    const request: CrearPedidoRequest = {
      idCliente: userId,
      idRestaurante: resumen.restauranteId,
      idDireccion: formValue.tipoEntrega === TipoEntrega.DELIVERY ? formValue.direccionId : null,
      tipoEntrega: formValue.tipoEntrega,
      metodoPago: formValue.metodoPago,
      productos: this.items().map(item => ({
        productoId: item.productoId,
        cantidad: item.cantidad,
        precioUnitario: item.precioConDescuento ? item.precioConDescuento : item.precio
      })),
      comentarios: formValue.comentarios || undefined
    };

    this.pedidoService.crearPedido(request).subscribe({
      next: (response) => {
        if (formValue.metodoPago === MetodoPago.MERCADO_PAGO && response.initPoint) {
          this.toastService.success('Redirigiendo a Mercado Pago...');
 
          this.carritoService.vaciarCarrito();
          const urlPago = response.sandboxInitPoint || response.initPoint;
          window.location.href = urlPago;
          
        } else if (formValue.metodoPago === MetodoPago.EFECTIVO) {
          this.toastService.success('✅ Pedido confirmado - Pago en efectivo al recibir');
          this.carritoService.vaciarCarrito();
          this.router.navigate(['/cliente/pedidos']);
        }

        this.isProcessing.set(false);
      },
      error: () => {
        this.isProcessing.set(false);
      }
    });
  }

  agregarDireccion(): void {
    this.router.navigate(['/cliente/my-address'], {
      queryParams: { returnUrl: '/checkout' }
    });
  }

  private calcularCostoDelivery(direccionId: number): void {
    const restauranteId = this.resumenConEnvio().restauranteId;
    
    if (!restauranteId) {
      this.costoDeliveryCalculado.set(0);
      return;
    }

    // Obtener dirección seleccionada
    const direccion = this.direcciones().find(d => d.id === direccionId);
    
    if (!direccion || !direccion.latitud || !direccion.longitud) {
      this.costoDeliveryCalculado.set(0);
      return;
    }

    // Obtener datos completos del restaurante
    this.restauranteService.findById(restauranteId).subscribe({
      next: (restaurante) => {
        if (!restaurante.direccion?.latitud || !restaurante.direccion?.longitud) {
          this.costoDeliveryCalculado.set(0);
          return;
        }

        // Calcular distancia
        const distanciaKm = this.geolocationService.calcularDistancia(
          { latitud: restaurante.direccion.latitud, longitud: restaurante.direccion.longitud },
          { latitud: direccion.latitud, longitud: direccion.longitud }
        );

        // Obtener costo de delivery del backend
        this.configuracionService.calcularCostoDelivery(distanciaKm).subscribe({
          next: (costo) => {
            this.costoDeliveryCalculado.set(costo);
          },
          error: () => {
            // Fallback: usar costo base
            this.costoDeliveryCalculado.set(2000);
          }
        });
      },
      error: () => {
        this.costoDeliveryCalculado.set(0);
      }
    });
  }

  volver(): void {
    this.router.navigate(['/cliente/carrito']);
  }
}