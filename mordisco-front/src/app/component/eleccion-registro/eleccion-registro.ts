import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-eleccion-registro',
  imports: [RouterLink],
  templateUrl: './eleccion-registro.html',
  styleUrl: './eleccion-registro.css'
})
export class EleccionRegistro {

  constructor(private router : Router){}

  seleccionarRol(id : number){
    this.router.navigate(['/registro'],{
      queryParams: {rol : id}
    })
  }
}
