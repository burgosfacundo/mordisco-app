import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";
import UserCard from '../../models/user/user-card';
import UserPedido from '../../models/user/user-pedido';

@Component({
  selector: 'app-usuario-card-component',
  imports: [RouterLink],
  templateUrl: './usuario-card-component.html'
})
export class UsuarioCardComponent {
  @Input() usuario!: UserCard;

  getInitials(): string {
    const first = this.usuario.nombre?.charAt(0) || '';
    const last = this.usuario.apellido?.charAt(0) || '';
    return (first + last).toUpperCase() || 'U';
  }
}
