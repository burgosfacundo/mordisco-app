import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import Menu from "../../models/menu/menu";
import { environment } from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class MenuService {
    private http : HttpClient = inject(HttpClient)

    getByRestauranteId(id : number) : Observable<Menu>{
        return this.http.get<Menu>(`${environment.apiUrl}/menus/${id}`)
    }

    save(restauranteId : number,menu : Menu) : Observable<string>{
        return this.http.post<string>(`${environment.apiUrl}/menus/${restauranteId}`,menu)
    }

    deleteByRestauranteId(id: number) : Observable<string>{
        return this.http.delete<string>(`${environment.apiUrl}/menus/${id}`)
    }
}