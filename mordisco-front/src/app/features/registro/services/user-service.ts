import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import UserProfile from '../../../models/user/user-profile';
import UserProfileEdit from '../../../models/user/user-profile-edit';
import UserCard from '../../../models/user/user-card';
import PaginationResponse from '../../../models/pagination/pagination-response';
import UserRegister from '../model/user-register';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http : HttpClient = inject(HttpClient)

  getAll(page : number, size : number) : Observable<PaginationResponse<UserCard>>{
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<UserCard>>(`${environment.apiUrl}/usuarios`, { params });
  }

  getMe() : Observable<UserProfile>{
    return this.http.get<UserProfile>(`${environment.apiUrl}/usuarios/me`)
  }


  post(userData : UserRegister) : Observable<string>{
      return this.http.post(
        `${environment.apiUrl}/usuarios/save`,
         userData, { responseType: 'text' as const }
      )
    }

  updateMe(user : UserProfileEdit) : Observable<UserProfile>{
    return this.http.patch<UserProfile>(`${environment.apiUrl}/usuarios/me`, user)
  }

  deleteMe(){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/me`)
  }
}
