import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import CalificacionRequestDTO from "../../models/calificacion/calificacion-request-dto";
import CalificacionRestauranteReponse from "../../models/calificacion/calificacion-restaurante-response";
import PaginationResponse from "../../models/pagination/pagination-response";

@Injectable({
  providedIn: 'root'
})
export class CalificacionService {
    private http : HttpClient = inject(HttpClient)

    save(dto : CalificacionRequestDTO) : Observable<string>{
        return this.http.post<string>(`${environment.apiUrl}/calificaciones/save`,dto)
    }

    getAllByRestauranteId(id : number,page : number, size : number) : Observable<PaginationResponse<CalificacionRestauranteReponse>>{
        const params = new HttpParams().set('page',page).set('size',size);
        return this.http.get<PaginationResponse<CalificacionRestauranteReponse>>(`${environment.apiUrl}/calificaciones/${id}`,{params})
    }

    delete(idCalificacion : number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/calificaciones/${idCalificacion}`)
    }
}