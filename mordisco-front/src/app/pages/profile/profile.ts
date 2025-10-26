import { Component } from '@angular/core';
import { ViewProfile } from "../../components/view-profile/view-profile";

@Component({
  selector: 'app-profile',
  imports: [ViewProfile],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {

}
