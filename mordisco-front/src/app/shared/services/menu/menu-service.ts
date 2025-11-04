import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import Menu from "../../models/menu/menu-response";
import { environment } from "../../../../environments/environment";
import MenuResponse from "../../models/menu/menu-response";

@Injectable({
  providedIn: 'root'
})
export class MenuService {
    private http : HttpClient = inject(HttpClient)

    getByRestauranteId(id : number) : Observable<MenuResponse>{
        return this.http.get<MenuResponse>(`${environment.apiUrl}/menus/${id}`)
    }

    save(restauranteId : number,nombreMenu : string) : Observable<string>{
        const params = new HttpParams().set('nombre',nombreMenu)
        return this.http.post<string>(`${environment.apiUrl}/menus/${restauranteId}`,{params})
    }

    update(restauranteId : number,nombreMenu : string) :Observable<string> {
        const params = new HttpParams().set('nombre',nombreMenu)
        return this.http.patch<string>(`${environment.apiUrl}/menus/${restauranteId}`,{params})
    }

    deleteByRestauranteId(id: number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/menus/${id}`)
    }
}