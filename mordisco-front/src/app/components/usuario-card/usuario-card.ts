import { Component, Input } from '@angular/core';
import User from '../../models/user';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-usuario-card',
  imports: [RouterLink],
  templateUrl: './usuario-card.html',
  styleUrls: ['./usuario-card.css']
})
export class UsuarioCard {
    @Input() usuario!: User;

}
