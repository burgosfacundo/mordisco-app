import { Component, inject, input, OnInit, output, signal } from '@angular/core';
import { ToastService } from '../../../core/services/toast-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { FormValidationService } from '../../../shared/services/form-validation-service';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/configuracion-sistema-response-dto';
import { Router } from '@angular/router';
import {
  DECIMAL_PATTERN,
  PORCENTAJE_GANANCIAS_RESTAURANTE_MIN,
  PORCENTAJE_GANANCIAS_RESTAURANTE_MAX,
  RADIO_MAXIMO_ENTREGA_MIN,
  RADIO_MAXIMO_ENTREGA_MAX,
  COSTO_BASE_DELIVERY_MIN,
  COSTO_BASE_DELIVERY_MAX,
  COSTO_POR_KILOMETRO_MIN,
  COSTO_POR_KILOMETRO_MAX,
  MONTO_MINIMO_PEDIDO_MIN,
  MONTO_MINIMO_PEDIDO_MAX,
  PORCENTAJE_GANANCIAS_REPARTIDOR_MIN,
  PORCENTAJE_GANANCIAS_REPARTIDOR_MAX
} from '../../../shared/validators/validation-constants';

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
      porcentajeGananciasRestaurante: ['', [
        Validators.required,
        Validators.min(PORCENTAJE_GANANCIAS_RESTAURANTE_MIN),
        Validators.max(PORCENTAJE_GANANCIAS_RESTAURANTE_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]],
      radioMaximoEntrega: ['', [
        Validators.required,
        Validators.min(RADIO_MAXIMO_ENTREGA_MIN),
        Validators.max(RADIO_MAXIMO_ENTREGA_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]],
      costoBaseDelivery: ['', [
        Validators.required,
        Validators.min(COSTO_BASE_DELIVERY_MIN),
        Validators.max(COSTO_BASE_DELIVERY_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]],
      costoPorKilometro: ['', [
        Validators.required,
        Validators.min(COSTO_POR_KILOMETRO_MIN),
        Validators.max(COSTO_POR_KILOMETRO_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]],
      montoMinimoPedido: ['', [
        Validators.required,
        Validators.min(MONTO_MINIMO_PEDIDO_MIN),
        Validators.max(MONTO_MINIMO_PEDIDO_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]],
      porcentajeGananciasRepartidor: ['', [
        Validators.required,
        Validators.min(PORCENTAJE_GANANCIAS_REPARTIDOR_MIN),
        Validators.max(PORCENTAJE_GANANCIAS_REPARTIDOR_MAX),
        Validators.pattern(DECIMAL_PATTERN)
      ]]
    });
  }

  private loadConfiguracionData(): void {
    this.csService.getConfiguracion().subscribe({
      next:(data)=> {
        this.configForm.patchValue({
          porcentajeGananciasRestaurante: data.porcentajeGananciasRestaurante,
          radioMaximoEntrega: data.radioMaximoEntrega,
          costoBaseDelivery: data.costoBaseDelivery,
          costoPorKilometro: data.costoPorKilometro,
          montoMinimoPedido: data.montoMinimoPedido,
          porcentajeGananciasRepartidor: data.porcentajeGananciasRepartidor
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




