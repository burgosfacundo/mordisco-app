import { Component, OnInit } from '@angular/core';
import User from '../../models/user';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../services/userService/user-service';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit{

  usuario? : User

  constructor(private uSerive : UserService, private aRoute : ActivatedRoute){}

  ngOnInit(): void {

   /* const IDUser = this.aRoute.snapshot.params['id']*/
    const IDUser = 1
    this.uSerive.getUserByID(IDUser).subscribe({
      next:(data) => this.usuario=data,
      error: (e)=> console.log(e)
    })
    
  }

}
