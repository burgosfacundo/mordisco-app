import { Component } from '@angular/core';
import { EditProfile } from "../../components/edit-profile/edit-profile";

@Component({
  selector: 'app-profile',
  imports: [EditProfile],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {

}
