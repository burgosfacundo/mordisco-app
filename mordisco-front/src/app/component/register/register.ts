import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario/usuario-service';
import Usuario from '../../models/Usuario';
import { ActivatedRoute, Router } from '@angular/router';
import { FormDirecciones } from '../form-direcciones/form-direcciones';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, FormDirecciones],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register implements OnInit{

  formRegister! : FormGroup
  rolSeleccionado? : number

  constructor(private fb : FormBuilder, public uService : UsuarioService, private aRoute : ActivatedRoute, private router : Router){
  }

  ngOnInit(): void {

    this.aRoute.queryParams.subscribe(params => {
    const rol = params['rol']
     if (rol) {
      this.rolSeleccionado = +rol
    }else {
      this.router.navigate(['/eleccion-usuario'])
    }
    
    this.inicializarFormulario();})
  }

  inicializarFormulario(){
     this.formRegister = this.fb.group({
      nombre: [''],
      apellido: [''],
      telefono: [''],
      email: [''],
      password: [''],
      rolId: [this.rolSeleccionado], 
      direccion: this.fb.group({
          calle: [''],
          numero: [''],
          piso: [''],
          depto: [''],
          codigoPostal: [''],
          referencias: [''],
          latitud: [''],
          longitud: [''],
          ciudad: ['']})    
    });
  }

  manejarRegistro(){
    const datosCompletos = this.formRegister.value;
    console.log('✅ Objeto Completo del Formulario a Enviar:', datosCompletos); 
    this.uService.createUsuario(datosCompletos).subscribe({
      next : (data) => console.log(data),
      error:(e) => console.log(e)
    })
  }

}
