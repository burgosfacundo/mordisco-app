import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { FormValidationService } from '../../../shared/services/form-validation-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import CalificacionPedidoRequestDTO from '../../../shared/models/calificacion/calificacion-pedido-request-dto';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';

@Component({
  selector: 'app-calificacion-form-pedido-component',
  imports: [ReactiveFormsModule],
  templateUrl: './calificacion-form-pedido-component.html',
})
export class CalificacionFormPedidoComponent implements OnInit{
  private router = inject(Router)
  private _snackBar = inject(MatSnackBar)
  private cService = inject(CalificacionService)
  private validationService = inject(FormValidationService)
  private fb = inject(FormBuilder)
  formCalificacionPedido! : FormGroup
  pedido = input<number|null>()

  isSubmitting = signal(false);

  ngOnInit(): void {
    this.inicializarFormularioPedido()
  }
  
  inicializarFormularioPedido(): void {
    this.formCalificacionPedido = this.fb.group({
      puntajeComida : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      puntajeTiempo : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      puntajePackaging : ['', [Validators.required,Validators.pattern(/^[1-5]$/)]],
      comentario : ['', Validators.maxLength(250)]
    }) 
  }

  manejarEnvioPedido() : void{
    if (this.formCalificacionPedido.invalid) return;

    this.isSubmitting.set(true);
    const { puntajeComida, puntajeTiempo, puntajePackaging, comentario } = this.formCalificacionPedido.getRawValue();
    const idPed = Number(this.pedido())

    if(!idPed){
      this._snackBar.open('❌ ID no válido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/pedidos']);
      return;
    }

    const calificacionRequest : CalificacionPedidoRequestDTO = {
      pedidoId : idPed,
      puntajeComida,
      puntajeTiempo,
      puntajePackaging,
      comentario
    }
    
  
    this.cService.calificarPedido(calificacionRequest).subscribe({
      next : () => {
        this._snackBar.open('✅ Calificacion registrada correctamente', '',{duration: 3000})
        this.router.navigate(['cliente/pedidos/detalle',idPed])
      },
      error:(e) => {
        console.error(e);
        this._snackBar.open('❌ Ocurrió un error. Intentelo en unos minutos', '',{duration: 3000})
        this.router.navigate(['cliente/pedidos']);
      }
    })
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.formCalificacionPedido.get(fieldName),
      fieldName
    );
  }
}

