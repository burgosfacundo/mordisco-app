import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";
import UserProfile from '../../models/user/user-profile';
import UserCard from '../../models/user/user-card';

@Component({
  selector: 'app-usuario-card',
  imports: [RouterLink],
  templateUrl: './usuario-card.html',
  styleUrls: ['./usuario-card.css']
})
export class UsuarioCard {
    @Input() usuario!: UserCard;
}
