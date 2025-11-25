import { Component, inject, OnInit } from '@angular/core';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/ConfiguracionSistemaResponseDTO';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfiguracionFormComponent } from "../configuracion-form-component/configuracion-form-component";

@Component({
  selector: 'app-configuracion-form-page',
  imports: [ConfiguracionFormComponent],
  templateUrl: './configuracion-form-page.html',
})
export class ConfiguracionFormPage implements OnInit{
  private csSerive = inject(ConfiguracionSistemaService)
  private router = inject(Router)
  private _snackBar = inject(MatSnackBar)
  config? : ConfiguracionSistemaResponseDTO 

  ngOnInit(): void {
    this.csSerive.getConfiguracion().subscribe({
      next: u => this.config = u,
      error: (error) => {
        console.log(error);
        this.openSnackBar('❌ Ocurrió un error al cargar la configuracion')
        this.router.navigate(['/'])
      }
    })
  }

  editarConfiguracion(){
    this.router.navigate(['/configuracion/edit'])
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }

}
