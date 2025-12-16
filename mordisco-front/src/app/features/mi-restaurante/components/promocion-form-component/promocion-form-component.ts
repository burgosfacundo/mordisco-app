import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { PromocionService } from '../../../../shared/services/promocion/promocion-service';
import PromocionRequest, { TipoDescuento, AlcancePromocion } from '../../../../shared/models/promocion/promocion-request';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';
import { MatDialog } from '@angular/material/dialog';
import ProductoResponse from '../../../../shared/models/producto/producto-response';
import { ProductoService } from '../../../../shared/services/productos/producto-service';
import { MenuService } from '../../../../shared/services/menu/menu-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';

@Component({
  selector: 'app-promocion-form-component',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule
  ],
  templateUrl: './promocion-form-component.html'
})
export class PromocionFormComponent implements OnInit {
  private promocionService = inject(PromocionService);
  private formValidationService = inject(FormValidationService)
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toastService = inject(ToastService);
  private dialog = inject(MatDialog)
  private productoService = inject(ProductoService);
  private menuService = inject(MenuService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private cdr = inject(ChangeDetectorRef);

  promocionForm!: FormGroup;
  isEditMode = false;
  promocionId?: number;
  restauranteId?: number;
  isSubmitting = false;

  // Enums para el template
  tiposDescuento = Object.values(TipoDescuento);
  alcancesPromocion = Object.values(AlcancePromocion);
  AlcancePromocion = AlcancePromocion;

  // Productos del restaurante
  productos: ProductoResponse[] = [];
  productosSeleccionados: Set<number> = new Set();

  // Warning state para descuentos altos (>50%)
  showHighDiscountWarning: boolean = false;
  highDiscountWarningMessage: string = '';

  // Fecha mínima para calendarios (hoy)
  minDate = new Date();

  // Fechas dinámicas para validación cruzada
  minFechaFin: Date | null = null;
  maxFechaInicio: Date | null = null;

  ngOnInit(): void {
    this.initForm();

    this.promocionId = this.route.snapshot.params['id'];
    this.restauranteId = this.route.snapshot.params['idRestaurante']

    // Si no hay restauranteId en la ruta (modo edición), obtenerlo del usuario autenticado
    if (!this.restauranteId) {
      this.obtenerRestauranteId();
    } else {
      // Si hay restauranteId en la ruta (modo creación), cargar productos
      this.cargarProductos();
    }

    if (this.promocionId) {
      this.isEditMode = true;
      this.cargarPromocion();
    }

    // Escuchar cambios en tipo de descuento para actualizar validaciones
    this.promocionForm.get('tipoDescuento')?.valueChanges.subscribe(() => {
      this.actualizarValidacionesDescuento();
    });

    // Escuchar cambios en fechaInicio para actualizar minFechaFin
    this.promocionForm.get('fechaInicio')?.valueChanges.subscribe((fechaInicio) => {
      if (fechaInicio) {
        // La fecha fin puede ser el mismo día (permite promociones de un solo día)
        this.minFechaFin = new Date(fechaInicio);
      } else {
        this.minFechaFin = null;
      }
    });

    // Escuchar cambios en fechaFin para actualizar maxFechaInicio
    this.promocionForm.get('fechaFin')?.valueChanges.subscribe((fechaFin) => {
      if (fechaFin) {
        // La fecha inicio puede ser el mismo día (permite promociones de un solo día)
        this.maxFechaInicio = new Date(fechaFin);
      } else {
        this.maxFechaInicio = null;
      }
    });

    // Re-validar cuando cambia alcance (afecta qué productos se consideran)
    this.promocionForm.get('alcance')?.valueChanges.subscribe(() => {
      this.promocionForm.get('descuento')?.updateValueAndValidity();
      this.checkHighDiscountWarning();
    });

    // Re-validar cuando cambia el valor del descuento
    this.promocionForm.get('descuento')?.valueChanges.subscribe(() => {
      this.checkHighDiscountWarning();
    });
  }

  initForm(): void {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    
    this.promocionForm = this.fb.group({
      tipoDescuento: ['', Validators.required],
      descripcion: ['', [Validators.required, Validators.maxLength(255)]],
      descuento: ['', [Validators.required, Validators.min(1)]],
      alcance: ['', Validators.required],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]],
      productosIds: [[]]
    }, {
      validators: [this.validarFechas.bind(this)]
    });
  }
  
  // Validador personalizado para fechas
  validarFechas(group: FormGroup): {[key: string]: any} | null {
    const fechaInicio = group.get('fechaInicio')?.value;
    const fechaFin = group.get('fechaFin')?.value;

    if (!fechaInicio || !fechaFin) {
      return null;
    }

    const inicio = new Date(fechaInicio);
    const fin = new Date(fechaFin);
    inicio.setHours(0, 0, 0, 0);
    fin.setHours(0, 0, 0, 0);

    // Solo validar que fecha fin >= fecha inicio (permite promociones de un solo día)
    // La validación de fecha >= hoy se maneja en el datepicker con [min]
    if (fin < inicio) {
      return { fechaFinInvalida: true };
    }

    return null;
  }

  /**
   * Validador personalizado para descuentos de tipo MONTO_FIJO
   * Asegura que el descuento no supere el precio del producto más barato aplicable
   */
  validarMontoFijo(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
      const tipoDescuento = this.promocionForm?.get('tipoDescuento')?.value;
      const descuento = control.value;

      // Solo validar para MONTO_FIJO
      if (tipoDescuento !== TipoDescuento.MONTO_FIJO) {
        return null;
      }

      // No validar si descuento está vacío (lo maneja required validator)
      if (!descuento) {
        return null;
      }

      const minPrice = this.getApplicableMinPrice();

      // Si no podemos determinar el precio mínimo aún (productos no cargados), no bloquear
      if (minPrice === null) {
        return null;
      }

      // Validación dura: descuento debe ser menor al precio más barato
      if (descuento >= minPrice) {
        return {
          montoExcedesProducto: {
            descuento: descuento,
            precioMinimo: minPrice
          }
        };
      }

      return null;
    };
  }

  /**
   * Verifica si el descuento es alto (>50% del producto más barato) y muestra advertencia
   * Esta advertencia no bloquea el envío, solo alerta al usuario
   */
  checkHighDiscountWarning(): void {
    const tipoDescuento = this.promocionForm.get('tipoDescuento')?.value;
    const descuento = this.promocionForm.get('descuento')?.value;

    // Resetear estado de advertencia
    this.showHighDiscountWarning = false;
    this.highDiscountWarningMessage = '';

    // Solo verificar para MONTO_FIJO
    if (tipoDescuento !== TipoDescuento.MONTO_FIJO || !descuento) {
      return;
    }

    const minPrice = this.getApplicableMinPrice();

    if (minPrice === null) {
      return;
    }

    // Umbral de advertencia: 50% del precio del producto más barato
    const warningThreshold = minPrice * 0.5;

    if (descuento > warningThreshold && descuento < minPrice) {
      this.showHighDiscountWarning = true;
      const percentage = Math.round((descuento / minPrice) * 100);
      this.highDiscountWarningMessage =
        `Advertencia: El descuento representa el ${percentage}% del producto más barato ($${minPrice.toFixed(2)}). ` +
        `Verifica que sea correcto.`;
    }
  }

  actualizarValidacionesDescuento(): void {
    const tipoDescuento = this.promocionForm.get('tipoDescuento')?.value;
    const descuentoControl = this.promocionForm.get('descuento');

    if (tipoDescuento === TipoDescuento.PORCENTAJE) {
      // Porcentaje: entre 1 y 100
      descuentoControl?.setValidators([Validators.required, Validators.min(1), Validators.max(100)]);
    } else {
      // Monto fijo: mayor a 0, con validador personalizado
      descuentoControl?.setValidators([Validators.required, Validators.min(1), this.validarMontoFijo()]);
    }

    // Marcar como touched para que se muestren los errores
    descuentoControl?.markAsTouched();
    descuentoControl?.updateValueAndValidity();

    // Verificar condiciones de advertencia
    this.checkHighDiscountWarning();
  }

  obtenerRestauranteId(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.toastService.error('❌ Error: Usuario no autenticado');
      this.router.navigate(['/']);
      return;
    }
    
    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante: any) => {
        this.restauranteId = restaurante.id;
        // Cargar productos una vez que tenemos el restauranteId
        if (this.restauranteId) {
          this.cargarProductos();
        }
      },
      error: () => {
        this.toastService.error('❌ Error al obtener datos del restaurante');
        this.router.navigate(['/']);
      }
    });
  }

  cargarPromocion(): void {
    if (!this.promocionId) {
      this.router.navigate(['/restaurante']);
      return;
    }

    this.promocionService.getById(this.promocionId).subscribe({
      next: (p) => {
        // Inicializar productos seleccionados primero
        if (p.productosIds && p.productosIds.length > 0) {
          this.productosSeleccionados = new Set(p.productosIds);
        }

        // Usar setTimeout para asegurar que las opciones del @for se hayan renderizado
        setTimeout(() => {
          console.log(p.alcance);
          
          this.promocionForm.patchValue({
            tipoDescuento: p.tipoDescuento,
            descripcion: p.descripcion,
            descuento: p.descuento,
            alcance: p.alcance,
            fechaInicio: this.parseFecha(p.fechaInicio),
            fechaFin: this.parseFecha(p.fechaFin),
            productosIds: p.productosIds || []
          });



          // Actualizar validaciones para el tipo cargado
          this.actualizarValidacionesDescuento();

          // Marcar como pristine después de cargar datos
          this.promocionForm.markAsPristine();

          // Forzar detección de cambios para actualizar la vista
          this.cdr.detectChanges();
        }, 0);

        console.log(this.promocionForm);
        
      }
    });
  }

  onSubmit(): void {
    if (!this.promocionForm.valid) {
      this.markFormGroupTouched(this.promocionForm);

      // Mensajes de error específicos
      if (this.promocionForm.errors?.['fechaFinInvalida']) {
        this.toastService.warning('⚠️ La fecha de fin debe ser igual o posterior a la fecha de inicio');
        return;
      }

      this.toastService.warning('⚠️ Por favor completa todos los campos correctamente');
      return;
    }
    
    // Validar productos si es específica
    if (this.promocionForm.value.alcance === AlcancePromocion.PRODUCTOS_ESPECIFICOS 
        && this.productosSeleccionados.size === 0) {
      this.toastService.warning('⚠️ Debes seleccionar al menos un producto');
      return;
    }

    // Si no hay restauranteId, obtenerlo antes de continuar
    if (!this.restauranteId) {
      const userId = this.authService.currentUser()?.userId;
      
      if (!userId) {
        this.toastService.error('❌ Error: Usuario no autenticado');
        return;
      }
      
      this.restauranteService.getByUsuario(userId).subscribe({
        next: (restaurante: any) => {
          this.restauranteId = restaurante.id;
          // Llamar recursivamente ahora que tenemos el ID
          this.onSubmit();
        },
        error: () => {
          this.toastService.error('❌ Error al obtener datos del restaurante');
        }
      });
      return;
    }

    // Ahora sí proceder con el submit
    this.isSubmitting = true;
    
    const promocionData: PromocionRequest = {
      descripcion: this.promocionForm.value.descripcion,
      descuento: parseFloat(this.promocionForm.value.descuento),
      tipoDescuento: this.promocionForm.value.tipoDescuento,
      alcance: this.promocionForm.value.alcance,
      fechaInicio: this.formatDate(this.promocionForm.value.fechaInicio),
      fechaFin: this.formatDate(this.promocionForm.value.fechaFin),
      activa: true,
      productosIds: this.promocionForm.value.productosIds,
      restauranteId: this.restauranteId
    };

    if (this.isEditMode && this.promocionId) {
      this.promocionService.put(promocionData, this.promocionId).subscribe({
        next: () => {
          this.toastService.success('✅ Promoción actualizada correctamente');
          this.router.navigate(['/restaurante']);
        },
        error: () => {
          this.isSubmitting = false;
        }
      });
    } else {
      // Crear nueva promoción
      this.promocionService.save(promocionData).subscribe({
        next: () => {
          this.toastService.success('✅ Promoción creada correctamente');
          this.router.navigate(['/restaurante']);
        },
        error: () => {
          this.isSubmitting = false;
        }
      });
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private parseFecha(fecha: string): Date {
    const [year, month, day] = fecha.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      control?.markAsDirty();
    });
  }

  getError(fieldName: string): string | null {
    const control = this.promocionForm.get(fieldName);

    // Manejar error personalizado de MONTO_FIJO
    if (fieldName === 'descuento' && control?.errors?.['montoExcedesProducto']) {
      const error = control.errors['montoExcedesProducto'];
      return `El descuento no puede ser igual o mayor al producto más barato ($${error.precioMinimo.toFixed(2)})`;
    }

    return this.formValidationService.getErrorMessage(control, fieldName);
  }
  
  cargarProductos(): void {
    if (!this.restauranteId) return;
    
    // Primero obtener el menú del restaurante
    this.menuService.getByRestauranteId(this.restauranteId).subscribe({
      next: (menu: any) => {
        if (menu && menu.id) {
          // Luego cargar los productos del menú
          this.productoService.getAllByIdMenu(menu.id, 0, 100).subscribe({
            next: (response) => {
              this.productos = response.content;

              // Re-validar después de cargar productos (importante para modo edición)
              // En modo edición, el descuento se carga antes que los productos
              if (this.promocionForm.get('tipoDescuento')?.value === TipoDescuento.MONTO_FIJO) {
                this.promocionForm.get('descuento')?.updateValueAndValidity();
                this.checkHighDiscountWarning();
              }
            },
            error: (err: any) => {
              console.error('Error cargando productos:', err);
            }
          });
        }
      },
      error: (err: any) => {
        console.error('Error cargando menú:', err);
      }
    });
  }
  
  toggleProducto(productoId: number): void {
    if (this.productosSeleccionados.has(productoId)) {
      this.productosSeleccionados.delete(productoId);
    } else {
      this.productosSeleccionados.add(productoId);
    }

    // Actualizar el form control
    this.promocionForm.patchValue({
      productosIds: Array.from(this.productosSeleccionados)
    });

    // Re-validar descuento si es PRODUCTOS_ESPECIFICOS (el precio mínimo cambió)
    if (this.promocionForm.get('alcance')?.value === AlcancePromocion.PRODUCTOS_ESPECIFICOS) {
      this.promocionForm.get('descuento')?.updateValueAndValidity();
      this.checkHighDiscountWarning();
    }
  }
  
  isProductoSeleccionado(productoId: number): boolean {
    return this.productosSeleccionados.has(productoId);
  }

  /**
   * Obtiene el precio mínimo entre los productos seleccionados
   * @returns El precio mínimo o null si no hay productos seleccionados
   */
  getMinSelectedProductPrice(): number | null {
    if (this.productosSeleccionados.size === 0) return null;

    const selectedProducts = this.productos.filter(p =>
      this.productosSeleccionados.has(p.id)
    );

    if (selectedProducts.length === 0) return null;

    return Math.min(...selectedProducts.map(p => p.precio));
  }

  /**
   * Obtiene el precio mínimo entre todos los productos del menú
   * @returns El precio mínimo o null si no hay productos disponibles
   */
  getMinMenuPrice(): number | null {
    if (this.productos.length === 0) return null;
    return Math.min(...this.productos.map(p => p.precio));
  }

  /**
   * Obtiene el precio mínimo aplicable basado en el alcance
   * @returns El precio mínimo o null si no es aplicable
   */
  getApplicableMinPrice(): number | null {
    const alcance = this.promocionForm.get('alcance')?.value;

    if (alcance === AlcancePromocion.PRODUCTOS_ESPECIFICOS) {
      return this.getMinSelectedProductPrice();
    } else if (alcance === AlcancePromocion.TODO_MENU) {
      return this.getMinMenuPrice();
    }

    return null;
  }

  onCancel(): void {
    if (this.promocionForm.dirty) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: { mensaje: '¿Deseas salir sin guardar los cambios?' }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result === true) {
          this.router.navigate(['/restaurante']);
        }
      });
    } else {
      this.router.navigate(['/restaurante']);
    }
  }
}