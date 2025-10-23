import { Component } from '@angular/core';
import { EditProfile } from "../../components/edit-profile/edit-profile";
import { Footer } from "../../components/footer/footer";

@Component({
  selector: 'app-profile',
  imports: [EditProfile, Footer],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {

}
