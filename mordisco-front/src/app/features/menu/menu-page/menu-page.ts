import { Component, inject, OnInit } from '@angular/core';
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { ActivatedRoute } from '@angular/router';
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import { DetalleRestaurante } from "../detalle-restaurante/detalle-restaurante";
import { MatPaginator } from "@angular/material/paginator";
import { MenuComponent } from "../menu-component/menu-component";

@Component({
  selector: 'app-menu-page',
  imports: [DetalleRestaurante, MenuComponent],
  templateUrl: './menu-page.html',
  styleUrl: './menu-page.css'
})
export class MenuPage implements OnInit{

  private aroute : ActivatedRoute = inject(ActivatedRoute)
  private rSerivice : RestauranteService = inject(RestauranteService)

  restauranteID?: number
  restaurante? : RestauranteResponse
  isLoadingRestaurante = false


  ngOnInit(): void {
    this.restauranteID = this.aroute.snapshot.params['id']
    if(this.restauranteID){
      this.obtenterRestaurante(this.restauranteID)
      console.log(this.restaurante)
    }
  }

  obtenterRestaurante(id : number){
    this.rSerivice.findById(id).subscribe({
      next:(data)=> {console.log(data),
        this.restaurante=data
      }
    })
  }

  

}
