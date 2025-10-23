import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import User from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  http : HttpClient = inject(HttpClient)

  readonly API_URL ="http://localhost:3000/usuario" /*JSON SERVER */

  getUserByID(id : number){
    return this.http.get<User>(`${this.API_URL}/${id}`)
  }
  
}
