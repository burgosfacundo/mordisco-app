import { Component, inject, OnInit } from '@angular/core';
import { ConfiguracionSistemaService } from '../../../shared/services/configuracionSistema/configuracion-sistema-service';
import ConfiguracionSistemaResponseDTO from '../../../shared/models/configuracion/configuracion-sistema-response-dto';
import { Router } from '@angular/router';
import { ConfiguracionFormComponent } from "../configuracion-form-component/configuracion-form-component";

@Component({
  selector: 'app-configuracion-form-page',
  imports: [ConfiguracionFormComponent],
  templateUrl: './configuracion-form-page.html',
})
export class ConfiguracionFormPage implements OnInit{
  private csSerive = inject(ConfiguracionSistemaService)
  private router = inject(Router)
  config? : ConfiguracionSistemaResponseDTO 

  ngOnInit(): void {
    this.csSerive.getConfiguracion().subscribe({
      next: u => this.config = u,
      error: () => {
        this.router.navigate(['/'])
      }
    })
  }

  editarConfiguracion(){
    this.router.navigate(['/configuracion/edit'])
  }
}
