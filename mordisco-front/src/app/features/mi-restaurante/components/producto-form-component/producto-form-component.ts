import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductoService } from '../../../../shared/services/productos/producto-service';
import ProductoResponse from '../../../../shared/models/producto/producto-response';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import ProductoUpdate from '../../../../shared/models/producto/producto-update';
import ProductoRequest from '../../../../shared/models/producto/producto-request';

@Component({
  selector: 'app-producto-form-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-form-component.html'
})
export class ProductoFormComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private productoService = inject(ProductoService);
  private formValidationService = inject(FormValidationService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  protected productoForm!: FormGroup;
  protected isEditMode = false;
  private menuId?: number;
  private productoId?: number;
  private imagenId?: number;
  protected isLoading = false;
  protected isSubmitting = false;

  ngOnInit(): void {
    this.initializeForm();
    this.loadRouteData();
  }

  ngOnDestroy(): void {
    this.productoService.clearProductoToEdit();
  }

  private initializeForm(): void {
    this.productoForm = this.fb.group({
      nombreProducto: ['', [
        Validators.required, 
        Validators.minLength(3),
        Validators.maxLength(100)
      ]],
      descripcion: ['', [
        Validators.required, 
        Validators.minLength(10),
        Validators.maxLength(500)
      ]],
      precioUnitario: [0, [
        Validators.required, 
        Validators.min(0.01),
        Validators.max(999999)
      ]],
      disponible: [true, [Validators.required]],
      imagenUrl: ['', [
        Validators.required, 
        Validators.pattern(/^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i)
      ]]
    });
  }

  private loadRouteData(): void {
    this.route.params.subscribe(params => {
      const urlSegments = this.route.snapshot.url;
      
      if (params['id'] && urlSegments[1]?.path === 'editar') {
        this.isEditMode = true;
        this.productoId = +params['id'];
        this.cargarProducto(this.productoId);
      }else if (params['menuId'] && urlSegments[1]?.path === 'nuevo') {
        this.menuId = +params['menuId'];
        this.isEditMode = false;
        
        if (!this.menuId || isNaN(this.menuId)) {
          this.snackBar.open('⚠️ ID de menú inválido', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/mi-menu']);
        }
      }else {
        this.snackBar.open('⚠️ Ruta inválida', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/mi-menu']);
      }
    });
  }

  private cargarProducto(id: number): void {
    this.isLoading = true;
    
    this.productoService.getById(id).subscribe({
      next: (producto) => {
        if (!producto) {
          this.snackBar.open('❌ Producto no encontrado', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/mi-menu']);
          return;
        }
        
        this.llenarFormulario(producto);
        this.menuId = producto.idMenu;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar producto:', error);
        this.snackBar.open('❌ Error al cargar el producto', 'Cerrar', { duration: 3000 });
        this.isLoading = false;
        this.router.navigate(['/mi-menu']);
      }
    });
  }

  private llenarFormulario(p: ProductoResponse): void {
    this.imagenId = p.imagen?.id;

    this.productoForm.patchValue({
      nombreProducto: p.nombre || '',
      descripcion: p.descripcion || '',
      precioUnitario: p.precio || 0,
      disponible: p.disponible ?? true,
      imagenUrl: p.imagen?.url || ''
    });
  }
  
  onSubmit(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      this.snackBar.open('⚠️ Por favor completa todos los campos correctamente', 'Cerrar', { duration: 3000 });
      return;
    }

    if (!this.menuId) {
      this.snackBar.open('❌ ID de menú no disponible', 'Cerrar', { duration: 3000 });
      return;
    }

    this.isSubmitting = true;

    if (this.isEditMode && this.productoId && this.imagenId) {
      this.actualizarProducto();
    } else if (!this.isEditMode) {
      this.crearProducto();
    } else {
      this.snackBar.open('❌ Datos incompletos para la operación', 'Cerrar', { duration: 3000 });
      this.isSubmitting = false;
    }
  }

  private crearProducto(): void {
    const formValues = this.productoForm.value;
    
    const request: ProductoRequest = {
      idMenu: this.menuId!,
      nombre: formValues.nombreProducto.trim(),
      descripcion: formValues.descripcion.trim(),
      precio: Number(formValues.precioUnitario),
      disponible: formValues.disponible,
      imagen: {
        url: formValues.imagenUrl.trim(),
        nombre: formValues.nombreProducto.trim()
      }
    };

    this.productoService.post(request).subscribe({
      next: () => {
        this.snackBar.open('✅ Producto creado exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/mi-menu']);
      },
      error: (error) => {
        console.error('Error al crear producto:', error);
        const mensaje = error.error?.message || 'Error al crear el producto';
        this.snackBar.open(`❌ ${mensaje}`, 'Cerrar', { duration: 4000 });
        this.isSubmitting = false;
      }
    });
  }

  private actualizarProducto(): void {
    const formValues = this.productoForm.value;
    
    const update: ProductoUpdate = {
      nombre: formValues.nombreProducto.trim(),
      descripcion: formValues.descripcion.trim(),
      precio: Number(formValues.precioUnitario),
      disponible: formValues.disponible,
      imagen: {
        id: this.imagenId!,
        url: formValues.imagenUrl.trim(),
        nombre: formValues.nombreProducto.trim()
      }
    };

    this.productoService.update(update, this.productoId!).subscribe({
      next: () => {
        this.snackBar.open('✅ Producto actualizado exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/mi-menu']);
      },
      error: (error) => {
        console.error('Error al actualizar producto:', error);
        const mensaje = error.error?.message || 'Error al actualizar el producto';
        this.snackBar.open(`❌ ${mensaje}`, 'Cerrar', { duration: 4000 });
        this.isSubmitting = false;
      }
    });
  }

  getError(fieldName: string): string | null {
    return this.formValidationService.getErrorMessage(
      this.productoForm.get(fieldName),
      fieldName
    );
  }

  onCancel(): void {
    if (this.productoForm.dirty) {
      if (confirm('⚠️ ¿Deseas salir sin guardar los cambios?')) {
        this.router.navigate(['/mi-menu']);
      }
    } else {
      this.router.navigate(['/mi-menu']);
    }
  }
}