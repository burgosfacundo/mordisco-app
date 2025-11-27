import { Router } from "@angular/router";
import { Component, inject, input } from '@angular/core';
import UserCard from '../../models/user/user-card';
import UserPedido from '../../models/user/user-pedido';

@Component({
  selector: 'app-usuario-card-component',
  imports: [],
  templateUrl: './usuario-card-component.html'
})
export class UsuarioCardComponent {
  usuario = input<UserCard | UserPedido>();
  private router = inject(Router)

  getInitials(): string {
    const first = this.usuario()?.nombre.charAt(0) || '';
    const last = this.usuario()?.apellido.charAt(0) || '';
    return (first + last).toUpperCase() || 'U';
  }

  verDetalles(){
    this.router.navigate(['/admin/detalle-usuario/', this.usuario()?.rol, this.usuario()?.id])
  }
}
