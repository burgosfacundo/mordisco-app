import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DireccionService } from '../../services/direccion/direccion-service';
import { AuthService } from '../../auth/services/auth-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-direccion-form',
  imports: [ReactiveFormsModule],
  templateUrl: './direccion-form.html',
  styleUrl: './direccion-form.css'
})
export class DireccionForm implements OnInit{

  private fb : FormBuilder = inject(FormBuilder)
  private dService : DireccionService = inject(DireccionService)
  private auS : AuthService = inject(AuthService)
  private _snackbar : MatSnackBar = inject(MatSnackBar)
  private router : Router = inject(Router)

  formDirecciones! : FormGroup 
  private subscription : Subscription = new Subscription() //para obtener el observable de edicion
  modoEdicion = false
  idCurrUser ?: number 

  ngOnInit(): void {
    this.formDirecciones = this.fb.group({
      id : [null],
      calle : ['', [Validators.required, Validators.maxLength(50)]],
      numero : ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[0-9]+$/)]],
      piso: ['',[Validators.required, Validators.maxLength(15), Validators.pattern(/^[0-9]+$/)]],
      depto: ['',[Validators.required, Validators.maxLength(15), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      codigoPostal: ['',[Validators.required, Validators.maxLength(15)]],
      referencias : ['', Validators.maxLength(250)],
      latitud: [''],
      longitud :[''],
      ciudad:['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]]
    })

    const resp = this.auS.currentUser()
    this.idCurrUser= resp?.userId 
    this.subscription.add(
      this.dService.currentDir.subscribe(d => {
        if(d){
          // Modo Edición: Hay una dirección para cargar
          this.modoEdicion=true
          this.formDirecciones.patchValue(d)
        }else{
          // Modo Creación
          this.formDirecciones.reset({id : null})
        }
      })
    )

  }

  manejarEnvio(){
    if(this.formDirecciones.invalid) return;
    if(this.idCurrUser){
      const direccionLeida = this.formDirecciones.value

      if(this.modoEdicion){
        this.dService.updateDireccion(this.idCurrUser,direccionLeida).subscribe({
          next: (data) => {console.log(data),
            this.openSnackBar("Direccion editada correctamente")
            this.router.navigate(['/profile/my-address'])
          },error: (e) => {console.log(e),
            this.openSnackBar("No se ha podido editar la direccion")
            this.router.navigate(['/profile/my-address'])
          }
        })
      }else{
        this.dService.createDireccion(this.idCurrUser, direccionLeida).subscribe({
          next: (data) => {console.log(data),
            this.openSnackBar("Direccion creada exitosamente")
            this.router.navigate(['/profile/my-address'])
          },error:(e)=>{ console.log(e),
            this.openSnackBar("No se ha podido crear la direccion")
            this.router.navigate(['/profile/my-address'])
          }
        })
      }     
    }
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackbar.open(message, action, { duration: 3000 });
  }

}
