import { Component, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { FormValidationService } from '../../../shared/services/form-validation-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import CalificacionRepartidorRequestDTO from '../../../shared/models/calificacion/calificacion-repartidor-request-dto';
import { ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-calificacion-form-repartidor-component',
  imports: [ReactiveFormsModule],
  templateUrl: './calificacion-form-repartidor-component.html',
})
export class CalificacionFormRepartidorComponent {

  private router = inject(Router)
  private toastService = inject(ToastService)
  private cService = inject(CalificacionService)
  private validationService = inject(FormValidationService)
  private fb = inject(FormBuilder)
  formCalificacionRepartidor! : FormGroup
  pedido = input<number|null>()

  isSubmitting = signal(false);

  ngOnInit(): void {
    this.inicializarFormularioRepartidor()
  }

  inicializarFormularioRepartidor(): void {
    this.formCalificacionRepartidor = this.fb.group({
      puntajeAtencion : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      puntajeComunicacion : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      puntajeProfesionalismo : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      comentario : ['', Validators.maxLength(250)]
    }) 
  }

  manejarEnvioRepartidor() : void{
    if (this.formCalificacionRepartidor.invalid) return;

    this.isSubmitting.set(true);
    const { puntajeAtencion, puntajeComunicacion, puntajeProfesionalismo, comentario } = this.formCalificacionRepartidor.getRawValue();
    const idPed = Number(this.pedido())

    if(!idPed){
      this.router.navigate(['/pedidos']);
      return;
    }

    const calificacionRequest : CalificacionRepartidorRequestDTO = {
      pedidoId : idPed,
      puntajeAtencion,
      puntajeComunicacion,
      puntajeProfesionalismo,
      comentario
    }
  
    this.cService.calificarRepartidor(calificacionRequest).subscribe({
      next : () => {
        this.toastService.success('✅ Calificacion registrada correctamente')
        this.router.navigate(['cliente/pedidos/detalle',idPed])
      },
      error:() => {
        this.router.navigate(['cliente/pedidos']);
      }
    })
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.formCalificacionRepartidor.get(fieldName),
      fieldName
    );
  }
}
