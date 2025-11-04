import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import ProductoRequest from "../../models/producto/producto-request";
import PaginationResponse from "../../models/pagination/pagination-response";
import ProductoResponse from "../../models/producto/producto-response";
import ProductoUpdate from "../../models/producto/producto-update";

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private http : HttpClient = inject(HttpClient)

  post(dto : ProductoRequest) : Observable<string>{
    return this.http.post<string>(`${environment.apiUrl}/productos/save`,dto)
  }

  getAllByIdMenu(idMenu : number,page : number,size : number) : Observable<PaginationResponse<ProductoResponse>>{
    const params = new HttpParams().set('page',page).set('size',size).set('idMenu',idMenu)
    return this.http.get<PaginationResponse<ProductoResponse>>(`${environment.apiUrl}/productos`,{params})
  }

  update(dto : ProductoUpdate,id : number) : Observable<string> {
    return this.http.put<string>(`${environment}/productos/${id}`,dto)
  }

  delete(id : number) : Observable<string>{
    return this.http.delete<string>(`${environment.apiUrl}/productos/${id}`)
  }
}