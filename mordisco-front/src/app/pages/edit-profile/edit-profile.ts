import { Component } from '@angular/core';
import { Footer } from "../../components/footer/footer";
import { EditProfileForm } from "../../components/edit-profile-form/edit-profile-form";

@Component({
  selector: 'app-edit-profile',
  imports: [Footer, EditProfileForm],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css'
})
export class EditProfile {

}
