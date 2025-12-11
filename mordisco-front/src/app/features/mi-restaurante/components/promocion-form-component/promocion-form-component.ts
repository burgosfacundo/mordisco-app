import { Component, inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { PromocionService } from '../../../../shared/services/promocion/promocion-service';
import PromocionRequest, { TipoDescuento, AlcancePromocion } from '../../../../shared/models/promocion/promocion-request';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
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

  promocionForm!: FormGroup;
  isEditMode = false;
  promocionId?: number;
  restauranteId?: number;
  isSubmitting = false;

  // Enums para el template
  tiposDescuento = Object.values(TipoDescuento);
  alcancesPromocion = Object.values(AlcancePromocion);
  AlcancePromocion = AlcancePromocion; // Para usar en el template

  // Productos del restaurante
  productos: ProductoResponse[] = [];
  productosSeleccionados: Set<number> = new Set();

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
  }

  initForm(): void {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    
    this.promocionForm = this.fb.group({
      tipoDescuento: [TipoDescuento.PORCENTAJE, Validators.required],
      descripcion: ['', [Validators.required, Validators.maxLength(255)]],
      descuento: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      alcance: [AlcancePromocion.TODO_MENU, Validators.required],
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
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    inicio.setHours(0, 0, 0, 0);
    fin.setHours(0, 0, 0, 0);
    
    // Solo validar fecha inicio >= hoy si NO estamos en modo edición
    // En modo edición, permitimos mantener fechas pasadas
    if (!this.isEditMode && inicio < hoy) {
      return { fechaInicioAnterior: true };
    }

    // Fecha fin debe ser igual o posterior a fecha inicio (permite promociones de un solo día)
    if (fin < inicio) {
      return { fechaFinInvalida: true };
    }

    return null;
  }
  
  actualizarValidacionesDescuento(): void {
    const tipoDescuento = this.promocionForm.get('tipoDescuento')?.value;
    const descuentoControl = this.promocionForm.get('descuento');
    
    if (tipoDescuento === TipoDescuento.PORCENTAJE) {
      // Porcentaje: entre 1 y 100
      descuentoControl?.setValidators([Validators.required, Validators.min(1), Validators.max(100)]);
    } else {
      // Monto fijo: mayor a 0, sin límite superior
      descuentoControl?.setValidators([Validators.required, Validators.min(1)]);
    }
    
    descuentoControl?.updateValueAndValidity();
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
        this.promocionForm.patchValue({
          tipoDescuento: p.tipoDescuento,
          descripcion: p.descripcion,
          descuento: p.descuento,
          alcance: p.alcance,
          fechaInicio: new Date(p.fechaInicio),
          fechaFin: new Date(p.fechaFin),
          productosIds: p.productosIds || []
        });
        
        // Inicializar productos seleccionados
        if (p.productosIds && p.productosIds.length > 0) {
          this.productosSeleccionados = new Set(p.productosIds);
        }
      }
    });
  }

  onSubmit(): void {
    if (!this.promocionForm.valid) {
      this.markFormGroupTouched(this.promocionForm);
      
      // Mensajes de error específicos
      if (this.promocionForm.errors?.['fechaInicioAnterior']) {
        this.toastService.warning('⚠️ La fecha de inicio no puede ser anterior a hoy');
        return;
      }
      if (this.promocionForm.errors?.['fechaFinInvalida']) {
        this.toastService.warning('⚠️ La fecha de fin debe ser posterior a la fecha de inicio');
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

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      control?.markAsDirty();
    });
  }

  getError(fieldName: string): string | null {
    return this.formValidationService.getErrorMessage(
      this.promocionForm.get(fieldName),
      fieldName
    );
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
  }
  
  isProductoSeleccionado(productoId: number): boolean {
    return this.productosSeleccionados.has(productoId);
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