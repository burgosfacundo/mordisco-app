import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import UserProfile from '../../../shared/models/user/user-profile';
import UserProfileEdit from '../../../shared/models/user/user-profile-edit';
import UserCard from '../../../shared/models/user/user-card';
import PaginationResponse from '../../../shared/models/pagination/pagination-response';
import UserRegister from '../model/user-register';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import BajaLogisticaDTO from '../../../shared/models/BajaLogisticaRequestDTO';

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
      return this.http.post(`${environment.apiUrl}/usuarios/save`, userData, { responseType: 'text' as const })
  }

  updateMe(user : UserProfileEdit) : Observable<UserProfile>{
    return this.http.patch<UserProfile>(`${environment.apiUrl}/usuarios/me`, user)
  }

  deleteMe(){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/me`)
  }
  
  getUserByID(id : number) : Observable<UserProfile> { 
    return this.http.get<UserProfile>(`${environment.apiUrl}/usuarios/${id}`)
  }

  updateUser(actualizado : UserProfile) : Observable<UserProfile>{
    return this.http.put<UserProfile>(`${environment.apiUrl}/usuarios/${actualizado.id}`, actualizado)
  }

  getPedidosActivos(page : number, size : number, id : number): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/usuarios/${id}/pedidos-activos`, { params });
  }

  delete(id : number){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/${id}`)
  }

  findByRolId(idRol : number, page : number, size : number): Observable<PaginationResponse<UserCard>>{
    const params = new HttpParams().set('page', page).set('size', size);  
    return this.http.get<PaginationResponse<UserCard>>(`${environment.apiUrl}/rol/${idRol}`, {params})
  }

  darDeBaja(dto : BajaLogisticaDTO, idUsuario : number){
    return this.http.post<void>(`${environment.apiUrl}/usuarios/${idUsuario}/baja`, dto)
  }

  reactivar(idUsuario : number){
    return this.http.post<void>(`${environment.apiUrl}/usuarios/${idUsuario}/reactivar`, null)
  }

  filtrarUsuarios(
    search: string,
    bajaLogica: string,
    rol: string,
    page: number,
    size: number
  ): Observable<PaginationResponse<UserCard>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (bajaLogica && bajaLogica !== '') {
      params = params.set('bajaLogica', bajaLogica);
    }
    if (rol && rol !== '') {
      params = params.set('rol', rol);
    }

    return this.http.get<PaginationResponse<UserCard>>(
      `${environment.apiUrl}/usuarios/buscar`, 
      { params }
    );
  }
}
