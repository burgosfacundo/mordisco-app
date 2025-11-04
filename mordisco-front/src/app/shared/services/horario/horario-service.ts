import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import HorarioAtencionRequest from "../../models/horario/horario-atencion-request";
import HorarioAtencionResponse from "../../models/horario/horario-atencion-response";
import PaginationResponse from "../../models/pagination/pagination-response";

@Injectable({
  providedIn: 'root'
})
export class HorarioService {
    private http : HttpClient = inject(HttpClient)

    save(dto : HorarioAtencionRequest,idRestaurante : number) : Observable<string>{
        return this.http.post<string>(`${environment.apiUrl}/restaurantes/horarios/${idRestaurante}`,dto)
    }

    getAllByRestauranteId(id : number,page : number, size : number) : Observable<PaginationResponse<HorarioAtencionResponse>>{
        const params = new HttpParams().set('page',page).set('size',size);
        return this.http.get<PaginationResponse<HorarioAtencionResponse>>(`${environment.apiUrl}/restaurantes/horarios/${id}`,{params})
    }

    update(id : number, dto : HorarioAtencionRequest) : Observable<string>{
        return this.http.put<string>(`${environment.apiUrl}/restaurantes/horarios/${id}`,dto)
    }

    delete(idCalificacion : number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/calificaciones/${idCalificacion}`)
    }
}