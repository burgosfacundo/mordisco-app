import { Component, inject, input, OnInit, output, signal } from '@angular/core';
import { ToastService } from '../../../core/services/toast-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { FormValidationService } from '../../../shared/services/form-validation-service';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/configuracion-sistema-response-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-configuracion-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './configuracion-form-component.html',
})
export class ConfiguracionFormComponent implements OnInit{
  private fb = inject(FormBuilder);
  private csService = inject(ConfiguracionSistemaService);
  private toastService = inject(ToastService);
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
        })
      },
      error:() => {
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
        this.toastService.success('✅ Configuracion actualizada correctamente');
        this.router.navigate(['admin/configuracion'])
      },
      error: () => {
        this.isSubmitting.set(false);
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




