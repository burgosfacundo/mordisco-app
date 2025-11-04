import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Producto from '../../../../shared/models/producto/producto';

@Component({
  selector: 'app-producto-form-component',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-form-component.html',
  styleUrl: './producto-form-component.css'
})
export class ProductoFormComponent implements OnInit{
  productoForm: FormGroup;
  isEditMode = false;
  productoId?: number;
  imagenPreview: string | null = null;
  archivoImagen: File | null = null;
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
    // private productoService: ProductoService
  ) {
    this.productoForm = this.fb.group({
      nombreProducto: ['', [Validators.required, Validators.maxLength(50)]],
      descripcion: ['', [Validators.required, Validators.maxLength(255)]],
      precioUnitario: ['', [Validators.required, Validators.min(1)]],
      disponible: [true, [Validators.required]]
    });
  }
   ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.productoId = +params['id'];
        this.cargarProducto(this.productoId);
      }
    });
  }
    cargarProducto(id: number): void {
    this.isLoading = true;
    // Aquí llamarías a tu servicio
    // this.productoService.getProductoById(id).subscribe({
    //   next: (producto) => {
    //     this.llenarFormulario(producto);
    //     this.isLoading = false;
    //   },
    //   error: (error) => {
    //     this.errorMessage = 'Error al cargar el producto';
    //     this.isLoading = false;
    //   }
    // });
  }
    llenarFormulario(producto: Producto): void {
    this.productoForm.patchValue({
      nombreProducto: producto.nombreProducto,
      descripcion: producto.descripcion,
      precioUnitario: producto.precioUnitario,
      disponible: producto.disponible
    });
    
    if (producto.imagen) {
      this.imagenPreview = producto.imagen.url;
    }
  }
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.archivoImagen = input.files[0];
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagenPreview = e.target?.result as string;
      };
      reader.readAsDataURL(this.archivoImagen);
    }
  }
  onSubmit(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }
        this.isSubmitting = true;
    const formData = new FormData();
    
    formData.append('nombreProducto', this.productoForm.get('nombreProducto')?.value);
    formData.append('descripcion', this.productoForm.get('descripcion')?.value);
    formData.append('precioUnitario', this.productoForm.get('precioUnitario')?.value);
    formData.append('disponible', this.productoForm.get('disponible')?.value);
    
    if (this.archivoImagen) {
      formData.append('imagen', this.archivoImagen);
    }

    if (this.isEditMode && this.productoId) {
      // Modo edición
      // this.productoService.updateProducto(this.productoId, formData).subscribe({
      //   next: () => {
      //     this.router.navigate(['/productos']);
      //   },
      //   error: (error) => {
      //     this.errorMessage = 'Error al actualizar el producto';
      //     this.isSubmitting = false;
      //   }
      // });
    } else {
      // Modo creación
      // this.productoService.createProducto(formData).subscribe({
      //   next: () => {
      //     this.router.navigate(['/productos']);
      //   },
      //   error: (error) => {
      //     this.errorMessage = 'Error al crear el producto';
      //     this.isSubmitting = false;
      //   }
      // });
    }
  }

  // Método para obtener errores 
  getError(controlName: string): string | null {
    const control = this.productoForm.get(controlName);
    
    if (!control || !control.errors || !control.touched) {
      return null;
    }

    if (control.errors['required']) {
      return 'Este campo es obligatorio';
    }
    if (control.errors['minlength']) {
      const minLength = control.errors['minlength'].requiredLength;
      return `Mínimo ${minLength} caracteres`;
    }
    if (control.errors['min']) {
      return 'El precio debe ser mayor a 0';
    }

    return 'Error en el campo';
  }
}

