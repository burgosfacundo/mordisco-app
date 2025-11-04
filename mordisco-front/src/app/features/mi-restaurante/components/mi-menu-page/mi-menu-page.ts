import { Component, inject, OnInit } from '@angular/core';
import { MenuService } from '../../../../shared/services/menu/menu-service';
import Menu from '../../../../shared/models/menu/menu';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { AuthResponse } from '../../../auth/models/auth-response';
import { ProductoCardComponent } from "../../../../shared/components/producto-card-component/producto-card-component";
import { Router } from '@angular/router';
import ProductoResponse from '../../../../shared/models/producto/producto-response';

@Component({
  selector: 'app-mi-menu-page',
  imports: [ProductoCardComponent],
  templateUrl: './mi-menu-page.html',
  styleUrl: './mi-menu-page.css'
})
export class MiMenuPage implements OnInit{

  private mService : MenuService = inject(MenuService)
  private rService : RestauranteService = inject(RestauranteService)
  private aus : AuthService = inject(AuthService)
  private router : Router = inject(Router)
  menu? : Menu 
  idCurrUser? : number | undefined 
  isLoading = true

  ngOnInit(): void {
    const resp : AuthResponse | null = this.aus.getCurrentUser()
    this.idCurrUser = resp?.userId

    if(this.idCurrUser){
      this.rService.getByUsuario(this.idCurrUser).subscribe({
      next: (r) =>{
        if(!r){
          console.log("No existe ese restaurante")
          return
      }else{
        this.mService.getByRestauranteId(r.id).subscribe({
          next:(m)=> {
            if(!m){
              console.log("No existe el menu")
              return
            }else{
              this.menu = m
            }
          }
        })
      }},
      error: (e)=> console.log(e)
    })}}

  confirmarEliminacion(id : number) {
      const confirmado = confirm(
        '⚠️ ¿Estás segura/o de que querés eliminar este producto? Esta acción no se puede deshacer.'
      );

      if (confirmado) {
        this.eliminarProducto(id);
      }
  }
    
  eliminarProducto(id : number) {

  }

  editarProducto(producto : ProductoResponse){
    this.router.navigate(['/product/form-product']);
  }

  agregarProducto(){
    this.router.navigate(['/product/form-product']);
  }
}


