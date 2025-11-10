import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import ProductoRequest from "../../models/producto/producto-request";
import PaginationResponse from "../../models/pagination/pagination-response";
import ProductoResponse from "../../models/producto/producto-response";
import ProductoUpdate from "../../models/producto/producto-update";

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private http : HttpClient = inject(HttpClient)
  private _productoToEdit = signal<ProductoResponse | null>(null);
  productoToEdit = this._productoToEdit.asReadonly();

  post(dto : ProductoRequest) : Observable<string>{
    return this.http.post<string>(`${environment.apiUrl}/productos/save`,dto)
  }

  getAllByIdMenu(idMenu : number,page : number,size : number) : Observable<PaginationResponse<ProductoResponse>>{
    const params = new HttpParams().set('page',page).set('size',size).set('idMenu',idMenu)
    return this.http.get<PaginationResponse<ProductoResponse>>(`${environment.apiUrl}/productos`,{params})
  }

  getById(id : number ) : Observable<ProductoResponse>{
      return this.http.get<ProductoResponse>(`${environment.apiUrl}/productos/${id}`)
  }

  update(dto : ProductoUpdate,id : number) : Observable<string> {
    return this.http.put<string>(`${environment.apiUrl}/productos/${id}`,dto)
  }

  delete(id : number) : Observable<string>{
    return this.http.delete<string>(`${environment.apiUrl}/productos/${id}`)
  }
  
  setProductoToEdit(producto : ProductoResponse){
    this._productoToEdit.set(producto)
  }
      
  clearProductoToEdit(): void{
    this._productoToEdit.set(null)
  }
}