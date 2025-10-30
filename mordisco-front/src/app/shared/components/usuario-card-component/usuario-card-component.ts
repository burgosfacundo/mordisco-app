import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";
import UserProfile from '../../../models/user/user-profile';
import UserCard from '../../../models/user/user-card';

@Component({
  selector: 'app-usuario-card-component',
  imports: [RouterLink],
  templateUrl: './usuario-card-component.html',
  styleUrls: ['./usuario-card-component.css']
})
export class UsuarioCardComponent {
    @Input() usuario!: UserCard;
}
