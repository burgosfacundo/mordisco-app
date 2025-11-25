import { Component, inject } from '@angular/core';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/ConfiguracionSistemaResponseDTO';

@Component({
  selector: 'app-configuracion-page',
  imports: [],
  templateUrl: './configuracion-page.html',
  styleUrl: './configuracion-page.css'
})
export class ConfiguracionPage {
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
    this.router.navigate(['admin/configuracion/edit'])
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }

}

