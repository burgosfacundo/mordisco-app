import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../auth/services/auth-service';
import User from '../../models/user';
import Address from '../../models/address';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule],
  templateUrl: './user-form-component.html',
  styleUrl: './user-form-component.css'
})
export class UserFormComponent implements OnInit{
  private service : AuthService = inject(AuthService)
  private fb : FormBuilder = inject(FormBuilder)
  userForm! : FormGroup

  private _snackBar = inject(MatSnackBar);

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }


  ngOnInit(): void {
    this.inicializarFormulario()
  }

  inicializarFormulario(){
     this.userForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      apellido: ['', [Validators.required, Validators.maxLength(50)]],
      telefono: ['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[0-9]{8,15}$/)]],
      email: ['', [Validators.required, Validators.email,Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rolId: ['', Validators.required],
      direcciones: this.fb.array([this.buildAddressGroup()])
    });
  }

  private buildAddressGroup(): FormGroup {
    return this.fb.group({
      calle: ['', [Validators.required,Validators.maxLength(50)]],
      numero: ['', [Validators.required,Validators.maxLength(50)]],
      piso: ['',Validators.maxLength(20)],
      depto: ['',Validators.maxLength(20)],
      codigoPostal: ['', [Validators.required,Validators.maxLength(10)]],
      referencias: ['',Validators.maxLength(255)],
      latitud: [null, Validators.required],
      longitud: [null, Validators.required],
      ciudad: ['', [Validators.required,Validators.maxLength(50)]]
    })
  }

  get direccionesFA(): FormArray {
    return this.userForm.get('direcciones') as FormArray;
  }

  addDireccion(): void {
    this.direccionesFA.push(this.buildAddressGroup());
  }

  removeDireccion(index: number): void {
    if (this.direccionesFA.length > 1) {
      this.direccionesFA.removeAt(index);
    }
  }

  onSubmit(){
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const raw = this.userForm.getRawValue();

    const direcciones: Address[] = raw.direcciones.map((d: any) => ({
      ...d,
      latitud: Number(d.latitud),
      longitud: Number(d.longitud),
    }));

    const user: User = {
      id: raw.id, ///////BORRAR SI ME OLVIDO EN EL MERGE
      nombre: raw.nombre,
      apellido: raw.apellido,
      telefono: raw.telefono,
      email: raw.email,
      password: raw.password,
      rolId: Number(raw.rolId),
      direcciones: direcciones,
    };

    console.log(user)

    this.service.register(user).subscribe({
      next : () => {
        this.openSnackBar('✅ Usuario registrado correctamente', 'Continuar')
      },
      error:() => this.openSnackBar('❌ Ocurrió un error. Intentelo en unos minutos', 'Continuar')
    })
  }

}
