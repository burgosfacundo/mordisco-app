import { Component, inject, OnInit } from '@angular/core';
import { MenuService } from '../../../../shared/services/menu/menu-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { AuthResponse } from '../../../auth/models/auth-response';
import { Router } from '@angular/router';
import ProductoResponse from '../../../../shared/models/producto/producto-response';
import { ProductoCardComponent } from "../../../../shared/components/producto-card-component/producto-card-component";
import MenuResponse from '../../../../shared/models/menu/menu-response';
import { ProductoService } from '../../../../shared/services/productos/producto-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-mi-menu-page',
  imports: [ProductoCardComponent, MatPaginator],
  templateUrl: './mi-menu-page.html',
  styleUrl: './mi-menu-page.css'
})
export class MiMenuPage implements OnInit{

  private aus : AuthService = inject(AuthService)
  private router : Router = inject(Router)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  private mService : MenuService = inject(MenuService)
  private rService : RestauranteService = inject(RestauranteService)
  private pService : ProductoService = inject(ProductoService)

  menu? : MenuResponse 
  idCurrUser? : number | undefined 
  restLeido? : RestauranteResponse
  arrProductos? : ProductoResponse[]

  sizeProductos : number = 5;
  pageProductos: number = 0;
  lengthProductos : number = 5;

  isLoading = true

  ngOnInit(): void {
    const resp : AuthResponse | null = this.aus.getCurrentUser()
    this.idCurrUser = resp?.userId
    if(this.idCurrUser){
      this.findRestaurante_and_menu(this.idCurrUser)
    }
  }

  listarProductos(){
    if(this.menu){
      this.pService.getAllByIdMenu(this.menu?.id, this.pageProductos,this.sizeProductos).subscribe({
        next:(data)=> { this.arrProductos= data.content,
          this.lengthProductos=data.totalElements,
          this.isLoading=false
        },error:(e)=>{
          this._snackBar.open('❌ Error al cargar los productos','Cerrar' , { duration: 3000 });
          this.router.navigate(['/'])
        }
      })
      }
  }

  findRestaurante_and_menu(id : number){
    this.rService.getByUsuario(id).subscribe({
          next: (r) =>{ 
            if(!r){
              console.log("No existe ese restaurante"); return
            }else{
              this.mService.getByRestauranteId(r.id).subscribe({
                next:(m)=> {
                  if(!m){
                    console.log("No existe el menu"); return
                  }else{
                    this.menu=m
                    this.listarProductos()
                  }},
                error:(e)=> {console.log(e); alert("Tu restaurante no tiene menu") 
                }})
            }
          },error: (e)=> console.log(e)})
  }
  
  onPageChangeProductos(event: PageEvent): void {
    this.pageProductos = event.pageIndex
    this.sizeProductos = event.pageSize;
    if (this.idCurrUser){
      this.findRestaurante_and_menu(this.idCurrUser);
    }
  }

  confirmarEliminacion(id : number) {
      const confirmado = confirm(
        '⚠️ ¿Estás segura/o de que querés eliminar este producto? Esta acción no se puede deshacer.'
      );

      if (confirmado) {
        this.eliminarProducto(id);
      }
  }
    
  eliminarProducto(id : number) {
    if(!this.idCurrUser || !id){
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
        return
    }

    this.pService.delete(id).subscribe({
      next :(data) => {console.log(data),
        this.openSnackBar('Producto eliminado correctamente')
          this.listarProductos()
      },
      error: (e)=> {console.log(e),
        this.openSnackBar('❌ Ocurrió un error al eliminar el producto')}
    })
  }

  editarProducto(producto : ProductoResponse){
    this.pService.setProductoToEdit(producto)
    this.router.navigate(['/product/form-product']);
  }

  agregarProducto(){
    this.router.navigate(['/product/form-product']);
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  } 
    
}


