import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../../shared/services/auth-service';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { Router } from '@angular/router';
import { HorarioService } from '../../../../shared/services/horario/horario-service';
import HorarioAtencionResponse from '../../../../shared/models/horario/horario-atencion-response';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import HorarioAtencionRequest from '../../../../shared/models/horario/horario-atencion-request';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast-service';

@Component({
  selector: 'app-horario-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './horario-form-component.html'
})
export class HorarioFormComponent implements OnInit{

  private fb : FormBuilder = inject(FormBuilder)
  private toastService = inject(ToastService)
  private validationService : FormValidationService = inject(FormValidationService)
  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  private hService : HorarioService = inject(HorarioService)
  private router : Router = inject(Router)
  private formatTimePart = (value: number | string): string => { return String(value).padStart(2, '0')} // // Helper para asegurar dos dígitos (ej: 9 -> '09') Asegura que el valor sea un string y rellena a la izquierda con '0' hasta tener 2 caracteres
  private subscription : Subscription = new Subscription()

  formHorarioAtencion! : FormGroup
  horarioEditado? : HorarioAtencionResponse

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
      minuteC : ['',  [Validators.required, Validators.min(0), Validators.max(59), Validators.pattern('^[0-9]*$')]],
      cruzaMedianoche: [false]
    })

    const resp = this.aus.getCurrentUser()
    this.idCurrUser = resp?.userId
    if(this.idCurrUser){
      this.encontrarRestaurante(this.idCurrUser)
    }

    this.subscription.add( 
      this.hService.currentHor.subscribe(h  => {
      if(h){
        this.modoEdicion = true;

        const [horaA, minuteA] = (h.horaApertura ?? '').split(':').slice(0, 2);
        const [horaC, minuteC] = (h.horaCierre ?? '').split(':').slice(0, 2);

        this.formHorarioAtencion.patchValue({
          id: h.id ?? null,
          dia: h.dia ?? '',
          horaA: horaA ?? '',
          minuteA: minuteA ?? '',
          horaC: horaC ?? '',
          minuteC: minuteC ?? '',
          cruzaMedianoche: h.cruzaMedianoche ?? false
        });
      }else{
        this.modoEdicion = false
        this.formHorarioAtencion.reset({id : null, dia : '', horaA : '', minuteA : '', horaC : '', minuteC :'', cruzaMedianoche: false})
      }
      this.loaded.emit();
      })
    )
  
  }

  encontrarRestaurante(id : number){
    this.rService.getByUsuario(id).subscribe({
      next:(data)=> this.restaurante=data,
      error:(e)=>console.log(e)
    })

  }

  manejarEnvio(){
    if(this.formHorarioAtencion.invalid) return;
        
    this.isSubmitting.set(true);
        
    if(this.idCurrUser){
      const horario = this.formHorarioAtencion.value
      const horaApertura: string =`${this.formatTimePart(horario.horaA)}:${this.formatTimePart(horario.minuteA)}:00`;// Resultado: "HH:mm:00"
      const horaCierre: string =  `${this.formatTimePart(horario.horaC)}:${this.formatTimePart(horario.minuteC)}:00`;// Resultado: "HH:mm:00"
      let horarioParaBackend: HorarioAtencionRequest

      if(this.modoEdicion){
        horarioParaBackend = {
          dia: this.formHorarioAtencion.value.dia, 
          horaApertura: horaApertura,
          horaCierre: horaCierre,
          cruzaMedianoche: this.formHorarioAtencion.value.cruzaMedianoche
        }

        this.hService.update(this.formHorarioAtencion.value.id,horarioParaBackend).subscribe({
          next:()=>{
            this.hService.clearHorarioToEdit()
            this.toastService.success("✅ Horario editado correctamente")
            this.router.navigate(['/restaurante/horarios'])
          },error:()=> { 
            this.hService.clearHorarioToEdit()
            this.router.navigate(['/restaurante/horarios'])
          }
        })
      }else{
        horarioParaBackend = {
          dia: this.formHorarioAtencion.value.dia, 
          horaApertura: horaApertura,
          horaCierre: horaCierre,
          cruzaMedianoche: this.formHorarioAtencion.value.cruzaMedianoche
        }

        if(this.restaurante){
          this.hService.save(horarioParaBackend, this.restaurante?.id).subscribe({
            next:()=>{
              this.toastService.success("✅ Horario creado correctamente")
              this.router.navigate(['/restaurante/horarios'])
            },
            error:()=>{
            this.router.navigate(['/restaurante/horarios'])
          }
          })
        }
      }

    }      
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
    this.formHorarioAtencion.get(fieldName),
    fieldName);
  }
}
   