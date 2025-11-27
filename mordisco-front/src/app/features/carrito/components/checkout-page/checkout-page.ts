import { Component, inject, OnInit, signal, computed } from '@angular/core';
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
import { NotificationService } from '../../../../core/services/notification-service';

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
  private notificationService = inject(NotificationService);
  private carritoService = inject(CarritoService);
  private pedidoService = inject(PedidoService);
  private direccionService = inject(DireccionService);
  private authService = inject(AuthService);

  checkoutForm!: FormGroup;
  direcciones = signal<DireccionResponse[]>([]);
  isLoading = signal(false);
  isProcessing = signal(false);

  // Signal para el tipo de entrega actual
  tipoEntregaActual = signal<TipoEntrega>(TipoEntrega.DELIVERY);

  // Computed del carrito
  items = this.carritoService.items;
  
  readonly COSTO_ENVIO = 2000;

  // Computed para saber si debe cobrar envío
  cobraEnvio = computed(() => 
    this.tipoEntregaActual() === TipoEntrega.DELIVERY
  );

  // Computed para el resumen con envío dinámico
  resumenConEnvio = computed(() => {
    const items = this.items();
    const subtotal = items.reduce((sum, item) => 
      sum + (item.precio * item.cantidad), 0
    );
    
    const costoEnvio = this.cobraEnvio() ? this.COSTO_ENVIO : 0;
    const total = subtotal + costoEnvio;

    return {
      subtotal,
      costoEnvio,
      total,
      restauranteId: this.carritoService.resumen().restauranteId
    };
  });

  ngOnInit(): void {
    if (!this.carritoService.tieneItems()) {
      this.notificationService.success('El carrito está vacío');
      this.router.navigate(['/cliente/carrito']);
      return;
    }

    this.initializeForm();
    this.cargarDirecciones();
  }

  private initializeForm(): void {
    this.checkoutForm = this.fb.group({
      tipoEntrega: [TipoEntrega.DELIVERY, Validators.required],
      direccionId: [null, Validators.required],
      metodoPago: [MetodoPago.MERCADO_PAGO, Validators.required],
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
      }
      
      direccionControl?.updateValueAndValidity();
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
        this.notificationService.error('Por favor selecciona una dirección de entrega');
      } else {
        this.notificationService.error('Por favor completa todos los campos requeridos')
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
      this.notificationService.error('Error al procesar el pedido');
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
        precioUnitario: item.precio
      })),
      comentarios: formValue.comentarios || undefined
    };

    this.pedidoService.crearPedido(request).subscribe({
      next: (response) => {
        if (formValue.metodoPago === MetodoPago.MERCADO_PAGO && response.initPoint) {
          this.notificationService.success('Redirigiendo a Mercado Pago...');
 
          this.carritoService.vaciarCarrito();
          const urlPago = response.sandboxInitPoint || response.initPoint;
          window.location.href = urlPago;
          
        } else if (formValue.metodoPago === MetodoPago.EFECTIVO) {
          this.notificationService.success('✅ Pedido confirmado - Pago en efectivo al recibir');
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

  volver(): void {
    this.router.navigate(['/cliente/carrito']);
  }
}