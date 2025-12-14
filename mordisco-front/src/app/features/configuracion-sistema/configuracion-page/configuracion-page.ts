import { Component, inject } from '@angular/core';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { Router } from '@angular/router';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/configuracion-sistema-response-dto';

@Component({
  selector: 'app-configuracion-page',
  imports: [],
  templateUrl: './configuracion-page.html',
  styleUrl: './configuracion-page.css'
})
export class ConfiguracionPage {
  private csSerive = inject(ConfiguracionSistemaService)
  private router = inject(Router)
  config? : ConfiguracionSistemaResponseDTO 

  ngOnInit(): void {
    this.csSerive.getConfiguracion().subscribe({
      next: u => this.config = u,
      error: (error) => {
        console.log(error);
        this.router.navigate(['/'])
      }
    })
  }

  editarConfiguracion(){
    this.router.navigate(['admin/configuracion/edit'])
  }
}

