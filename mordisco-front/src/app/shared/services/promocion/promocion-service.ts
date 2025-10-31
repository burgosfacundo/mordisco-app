import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import PromocionRequest from "../../models/restaurante/promocion-request";
import PromocionResponse from "../../models/restaurante/promocion-response";


@Injectable({
  providedIn: 'root'
})
export class PromocionService {
    private http : HttpClient = inject(HttpClient)

    save(promocion : PromocionRequest) : Observable<string>{
        return this.http.post<string>(`${environment.apiUrl}/promociones/save`,promocion)
    }

    get(id : number) : Observable<PromocionResponse>{
        return this.http.get<PromocionResponse>(`${environment.apiUrl}/promociones/${id}`)
    }

    put(promocion : PromocionResponse) : Observable<string>{
        return this.http.put<string>(`${environment.apiUrl}/promociones/${promocion.id}`,promocion)
    }

    delete(idRestaurante : number , idPromocion : number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/promociones/${idRestaurante}/${idPromocion}`)
    }
}