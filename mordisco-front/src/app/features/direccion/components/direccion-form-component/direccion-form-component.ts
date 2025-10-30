import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DireccionService } from '../../services/direccion-service';

import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';

@Component({
  selector: 'app-direccion-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './direccion-form-component.html',
})
export class DireccionFormComponent implements OnInit{

  private fb : FormBuilder = inject(FormBuilder)
  private dService : DireccionService = inject(DireccionService)
  private auS : AuthService = inject(AuthService)
  private _snackbar : MatSnackBar = inject(MatSnackBar)
  private router : Router = inject(Router)
  private validationService = inject(FormValidationService)
  
  formDirecciones! : FormGroup 
  private subscription : Subscription = new Subscription()

  @Input() modoEdicion: boolean = false; // valor inicial por defecto
  @Output() modoEdicionChange = new EventEmitter<boolean>(); 
  
  isSubmitting = signal(false)
  idCurrUser ?: number 

  ngOnInit(): void {
    this.formDirecciones = this.fb.group({
      id : [null],
      calle : ['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      numero : ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[0-9]+$/)]],
      piso: ['',[Validators.maxLength(15), Validators.pattern(/^[0-9]+$/)]],
      depto: ['',Validators.maxLength(15)],
      codigoPostal: ['',[Validators.required, Validators.maxLength(15)]],
      referencias : ['', Validators.maxLength(250)],
      ciudad:['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]]
    })

    const resp = this.auS.currentUser()
    this.idCurrUser= resp?.userId 
    
    this.subscription.add(
      this.dService.currentDir.subscribe(d => {
        if(d){
          this.modoEdicion = true
          this.modoEdicionChange.emit(this.modoEdicion)
          this.formDirecciones.patchValue(d)
        }else{
          this.formDirecciones.reset({id : null})
        }
      })
    )
  }

  manejarEnvio(){
    if(this.formDirecciones.invalid) return;
    
    this.isSubmitting.set(true);
    
    if(this.idCurrUser){
      const direccionLeida = this.formDirecciones.value

      if(this.modoEdicion){
        this.dService.updateDireccion(this.idCurrUser,direccionLeida).subscribe({
          next: (data) => {console.log(data),
            this._snackbar.open("✅ Direccion editada correctamente",'',{duration: 3000})
            this.router.navigate(['/profile/my-address'])
          },error: (e) => {console.log(e),
            this._snackbar.open("❌ No se ha podido editar la direccion",'',{duration: 3000})
            this.router.navigate(['/profile/my-address'])
          }
        })
      }else{
        this.dService.createDireccion(this.idCurrUser, direccionLeida).subscribe({
          next: (data) => {console.log(data),
            this._snackbar.open("✅ Direccion creada exitosamente", "Continuar",{ duration: 3000 })
            this.router.navigate(['/profile/my-address'])
          },error:(e)=>{ console.log(e),
            this._snackbar.open("❌ No se ha podido crear la direccion", "Continuar", { duration: 3000 })
            this.router.navigate(['/profile/my-address'])
          }
        })
      }     
    }
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.formDirecciones.get(fieldName),
      fieldName);
    }
}
