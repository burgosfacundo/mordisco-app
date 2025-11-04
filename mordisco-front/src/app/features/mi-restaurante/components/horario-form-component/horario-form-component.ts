import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import HorarioAtencion from '../../../../shared/models/restaurante/horario-atencion';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-horario-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './horario-form-component.html',
  styleUrl: './horario-form-component.css'
})
export class HorarioFormComponent implements OnInit{

  private fb : FormBuilder = inject(FormBuilder)
  private validationService : FormValidationService = inject(FormValidationService)
  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  //private hService : HorarioService = inject(HorarioService)
  private router : Router = inject(Router)
  private formatTimePart = (value: number | string): string => { return String(value).padStart(2, '0')} // // Helper para asegurar dos dígitos (ej: 9 -> '09') Asegura que el valor sea un string y rellena a la izquierda con '0' hasta tener 2 caracteres

  formHorarioAtencion! : FormGroup
  private subscription : Subscription = new Subscription()
  horarioEditado? : HorarioAtencion
  restaurante? : RestauranteResponse
  idCurrUser ?: number 

  @Input() modoEdicion: boolean = false; // valor inicial por defecto
  @Output() loaded = new EventEmitter<void>();
  
  isSubmitting = signal(false)

  ngOnInit(): void {
    this.formHorarioAtencion = this.fb.group({
      id : [null],
      dia: ['', Validators.required],
      horaA : ['', [Validators.required, Validators.min(0), Validators.max(23), Validators.pattern('^[0-9]*$')]],
      minuteA : ['',  [Validators.required, Validators.min(0), Validators.max(59), Validators.pattern('^[0-9]*$')]],      
      horaC : ['', [Validators.required, Validators.min(0), Validators.max(23), Validators.pattern('^[0-9]*$')]],
      minuteC : ['',  [Validators.required, Validators.min(0), Validators.max(59), Validators.pattern('^[0-9]*$')]]
    })

    const resp = this.aus.getCurrentUser()
    this.idCurrUser = resp?.userId
/*
    this.subscription.add( 
      this.rService.currentHor.subscribe(h  => {
      if(h){
        this.modoEdicion = true
        this.formHorarioAtencion.patchValue(h)
      }else{
        this.modoEdicion = false
        this.formHorarioAtencion.reset({id : null, dia : '', horaA : '', minuteA : '', horaC : '', minuteC :''})
      }
      this.loaded.emit();
      })
    )
  */
  }

  manejarEnvio(){
    if(this.formHorarioAtencion.invalid) return;
        
    this.isSubmitting.set(true);
        
    if(this.idCurrUser){
      const horario = this.formHorarioAtencion.value
      const horaApertura: string =`${this.formatTimePart(horario.horaA)}:${this.formatTimePart(horario.minuteA)}:00`;// Resultado: "HH:mm:00"
      const horaCierre: string =  `${this.formatTimePart(horario.horaC)}:${this.formatTimePart(horario.minuteC)}:00`;// Resultado: "HH:mm:00"
      let horarioParaBackend: HorarioAtencion
/*
      if(this.modoEdicion){
        horarioParaBackend = {
          id : this.formHorarioAtencion.value.id,   
          dia: this.formHorarioAtencion.value.dia, 
          horaApertura: horaApertura,
          horaCierre: horaCierre}
        this.rService.setHorarioToEdit(horarioParaBackend)
      }else{
        horarioParaBackend = {
          dia: this.formHorarioAtencion.value.dia, 
          horaApertura: horaApertura,
          horaCierre: horaCierre
        }
        this.rService.setHorarioToEdit(horarioParaBackend)
      }
        this.router.navigate(['/horarios'])
*/
    }      
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
    this.formHorarioAtencion.get(fieldName),
    fieldName);
  }
}
   