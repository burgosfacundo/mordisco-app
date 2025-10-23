import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import User from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  readonly API_URL ="http://"
  constructor(private http:HttpClient){}

  getUserByID(id : number){
    return this.http.get<User>(`${this.API_URL}/${id}`)
  }
  
}
