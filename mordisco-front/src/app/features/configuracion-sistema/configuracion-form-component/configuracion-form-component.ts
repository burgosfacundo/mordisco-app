import { Component, inject, input, OnInit, output, signal, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormValidationService } from '../../../shared/services/form-validation-service';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/ConfiguracionSistemaResponseDTO';
import { Router } from '@angular/router';

@Component({
  selector: 'app-configuracion-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './configuracion-form-component.html',
})
export class ConfiguracionFormComponent implements OnInit{
  private fb = inject(FormBuilder);
  private csService = inject(ConfiguracionSistemaService);
  private snackBar = inject(MatSnackBar);
  protected validationService = inject(FormValidationService);
  private router = inject(Router)

  configuracion = input<ConfiguracionSistemaResponseDTO>();
  onSaved = output<void>();
  onCancelled = output<void>();

  configForm!: FormGroup;
  protected isSubmitting = signal(false);
  protected isEditMode = signal(false);

  ngOnInit(){
    this.initializeForm();
    this.loadConfiguracionData();
  }

  private initializeForm(): void {
    this.configForm = this.fb.group({
      comisionPlataforma: ['', [Validators.required, Validators.maxLength(2), Validators.pattern(/^[0-9]+$/)]],
      radioMaximoEntrega: ['', [Validators.required, Validators.maxLength(5),  Validators.pattern(/^[0-9]+$/)]],
      tiempoMaximoEntrega: ['', [Validators.required, Validators.maxLength(5),  Validators.pattern(/^[0-9]+$/)]],
      costoBaseDelivery:  ['', [Validators.required, Validators.maxLength(10),  Validators.pattern(/^[0-9]+$/)]],
      costoPorKilometro: ['', [Validators.required, Validators.maxLength(10),  Validators.pattern(/^[0-9]+$/)]],
      montoMinimoPedido:['', [Validators.required, Validators.maxLength(10),  Validators.pattern(/^[0-9]+$/)]],
      porcentajeGananciasRepartidor: ['', [Validators.required, Validators.maxLength(2), Validators.pattern(/^[0-9]+$/)]],
      modoMantenimiento : [true, [Validators.required]],
      mensajeMantenimiento : ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],

    });
  }

  private loadConfiguracionData(): void {
    this.csService.getConfiguracion().subscribe({
      next:(data)=> {
        this.configForm.patchValue({
          comisionPlataforma: data.comisionPlataforma,
          radioMaximoEntrega: data.radioMaximoEntrega,
          tiempoMaximoEntrega: data.tiempoMaximoEntrega,
          costoBaseDelivery: data.costoBaseDelivery,
          costoPorKilometro: data.costoPorKilometro,
          montoMinimoPedido: data.montoMinimoPedido,
          porcentajeGananciasRepartidor: data.porcentajeGananciasRepartidor,
          modoMantenimiento :  data.modoMantenimiento,
          mensajeMantenimiento : data.mensajeMantenimiento,
        })},error:() => {
          this.snackBar.open('❌ Ocurrió un error al cargar los datos del perfil','',{duration: 3000})
          this.router.navigate(['/'])
        }
    })
  }

  actualizarConfiguracion(): void {
    if (this.configForm.invalid) {
      this.configForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.csService.actualizarConfiguracion(this.configForm.value).subscribe({
      next: () => {
        this.snackBar.open('✅ Configuracion actualizada correctamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['admin/configuracion'])
      },
      error: (error) => {
        console.error('Error:', error);
        this.snackBar.open('❌ Error al actualizar la configuracion', 'Cerrar', { duration: 4000 });

      }
     });
    }
 
  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.configForm.get(fieldName),
      fieldName
    );
  }
}




