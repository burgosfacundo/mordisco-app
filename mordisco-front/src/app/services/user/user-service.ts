import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import User from '../../models/user/user-register';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import UserProfile from '../../models/user/user-profile';
import UserProfileEdit from '../../models/user/user-profile-edit.ts';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http : HttpClient = inject(HttpClient)

  getAll() : Observable<User[]>{
    return this.http.get<User[]>(`${environment.apiUrl}/usuarios`)
  }

  getMe() : Observable<UserProfile>{
    return this.http.get<UserProfile>(`${environment.apiUrl}/usuarios/me`)
  }

  updateMe(user : UserProfileEdit) : Observable<UserProfile>{
    return this.http.patch<UserProfile>(`${environment.apiUrl}/usuarios/me`, user)
  }

  deleteMe(){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/me`)
  }
}
