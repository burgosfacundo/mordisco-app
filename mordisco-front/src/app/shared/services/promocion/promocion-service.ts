import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import PromocionRequest from "../../models/promocion/promocion-request";
import PromocionResponse from "../../models/promocion/promocion-response";
import PaginationResponse from "../../models/pagination/pagination-response";


@Injectable({
  providedIn: 'root'
})
export class PromocionService {
    private http : HttpClient = inject(HttpClient)

    save(promocion : PromocionRequest) : Observable<string>{
        return this.http.post<string>(`${environment.apiUrl}/promociones/save`,promocion)
    }

    get(id : number, page : number, size : number) : Observable<PaginationResponse<PromocionResponse>>{
        const params = new HttpParams().set('page',page).set('size',size)
        return this.http.get<PaginationResponse<PromocionResponse>>(`${environment.apiUrl}/promociones/${id}`,{params})
    }

    put(promocion : PromocionRequest, id : number) : Observable<string>{
        return this.http.put<string>(`${environment.apiUrl}/promociones/${id}`,promocion)
    }

    delete(idRestaurante : number , idPromocion : number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/promociones/${idRestaurante}/${idPromocion}`)
    }
}