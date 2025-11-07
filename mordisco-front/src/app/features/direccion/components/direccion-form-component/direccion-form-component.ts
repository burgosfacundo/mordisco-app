import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { Subscription } from 'rxjs';
import { DireccionService } from '../../services/direccion-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import DireccionRequest from '../../../../shared/models/direccion/direccion-request';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-direccion-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './direccion-form-component.html',
})
export class DireccionFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dService = inject(DireccionService);
  private auS = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private _snackbar = inject(MatSnackBar);
  private router = inject(Router);
  private validationService = inject(FormValidationService);
  
  formDirecciones!: FormGroup;
  private subscription: Subscription = new Subscription();

  @Output() enviarDireccion: EventEmitter<DireccionRequest> = new EventEmitter<DireccionRequest>();
  @Input() modoEdicion: boolean = false;
  @Input() direccionNeed: boolean = false; // Para uso en formulario de restaurante
  @Output() loaded = new EventEmitter<void>();

  isSubmitting = signal(false);
  idCurrUser?: number;
  userRole?: string;
  restauranteId?: number;

  ngOnInit(): void {
    this.formDirecciones = this.fb.group({
      id: [null],
      calle: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      numero: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[0-9]+$/)]],
      piso: ['', [Validators.maxLength(15), Validators.pattern(/^[0-9]+$/)]],
      depto: ['', Validators.maxLength(15)],
      codigoPostal: ['', [Validators.required, Validators.maxLength(15)]],
      referencias: ['', Validators.maxLength(250)],
      ciudad: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]]
    });

    const resp = this.auS.currentUser();
    this.idCurrUser = resp?.userId;
    this.userRole = resp?.role;

    // Si es restaurante y no es para creación, cargar su dirección
    if (this.userRole === 'ROLE_RESTAURANTE' && !this.direccionNeed) {
      this.cargarDireccionRestaurante();
    }
    
    // Subscribirse a cambios de dirección a editar
    this.subscription.add(
      this.dService.currentDir.subscribe(d => {
        if (d) {
          this.modoEdicion = true;
          this.formDirecciones.patchValue(d);
        } else {
          this.formDirecciones.reset({
            id: null,
            calle: '',
            numero: '',
            piso: '',
            depto: '',
            codigoPostal: '',
            referencias: '',
            ciudad: ''
          });
        }
        this.loaded.emit();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private cargarDireccionRestaurante(): void {
    if (!this.idCurrUser) return;

    this.restauranteService.getByUsuario(this.idCurrUser).subscribe({
      next: (restaurante) => {
        this.restauranteId = restaurante.id;
        if (restaurante.direccion) {
          this.modoEdicion = true;
          this.formDirecciones.patchValue(restaurante.direccion);
        }
      },
      error: (error) => {
        console.error('Error al cargar restaurante:', error);
      }
    });
  }

  manejarEnvio(): void {
    if (this.formDirecciones.invalid) {
      this.formDirecciones.markAllAsTouched();
      return;
    }
    
    this.isSubmitting.set(true);
    
    if (!this.idCurrUser) {
      this._snackbar.open('❌ Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.isSubmitting.set(false);
      return;
    }

    const direccionLeida: DireccionRequest = this.formDirecciones.value;

    // Si es para el formulario de creación de restaurante, solo emitir
    if (this.direccionNeed) {
      this.ejecutarEnvio();
      this.isSubmitting.set(false);
      return;
    }

    // Si es restaurante, actualizar dirección del restaurante
    if (this.userRole === 'ROLE_RESTAURANTE' && this.restauranteId) {
      this.dService.updateDireccion(this.idCurrUser, direccionLeida).subscribe({
        next: () => {
          this.dService.clearDireccionToEdit();
          this._snackbar.open('✅ Dirección actualizada correctamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/mi-restaurante']);
        },
        error: (error) => {
          console.error('Error:', error);
          this.dService.clearDireccionToEdit();
          this._snackbar.open('❌ No se ha podido actualizar la dirección', 'Cerrar', { duration: 3000 });
          this.isSubmitting.set(false);
        }
      });
    } else {
      // Para clientes, usar el flujo normal
      if (this.modoEdicion) {
        const direccionCompleta = { ...direccionLeida, id: this.formDirecciones.value.id };
        this.dService.updateDireccion(this.idCurrUser, direccionCompleta as any).subscribe({
          next: () => {
            this.dService.clearDireccionToEdit();
            this._snackbar.open('✅ Dirección editada correctamente', 'Cerrar', { duration: 3000 });
            this.router.navigate(['/my-address']);
          },
          error: (error) => {
            console.error('Error:', error);
            this.dService.clearDireccionToEdit();
            this._snackbar.open('❌ No se ha podido editar la dirección', 'Cerrar', { duration: 3000 });
            this.isSubmitting.set(false);
          }
        });
      } else {
        this.dService.createDireccion(this.idCurrUser, direccionLeida).subscribe({
          next: () => {
            this._snackbar.open('✅ Dirección creada exitosamente', 'Cerrar', { duration: 3000 });
            this.router.navigate(['/my-address']);
          },
          error: (error) => {
            console.error('Error:', error);
            this._snackbar.open('❌ No se ha podido crear la dirección', 'Cerrar', { duration: 3000 });
            this.isSubmitting.set(false);
          }
        });
      }
    }
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.formDirecciones.get(fieldName),
      fieldName
    );
  }

  ejecutarEnvio(): void {
    this.enviarDireccion.emit({
      calle: this.formDirecciones.value.calle,
      numero: this.formDirecciones.value.numero,
      piso: this.formDirecciones.value.piso,
      depto: this.formDirecciones.value.depto,
      codigoPostal: this.formDirecciones.value.codigoPostal,
      referencias: this.formDirecciones.value.referencias,
      ciudad: this.formDirecciones.value.ciudad
    });
  }
}