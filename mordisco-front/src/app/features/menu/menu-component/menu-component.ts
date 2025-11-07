import { Component, inject, Input, OnInit } from '@angular/core';
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { MenuService } from '../../../shared/services/menu/menu-service';
import MenuResponse from '../../../shared/models/menu/menu-response';
import { Router } from '@angular/router';
import { ProductoService } from '../../../shared/services/productos/producto-service';
import ProductoResponse from '../../../shared/models/producto/producto-response';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { ProductoCardComponent } from "../../../shared/components/producto-card-component/producto-card-component";
import { ProductoCardWithAdd } from "../../../shared/components/producto-card-with-add/producto-card-with-add";

@Component({
  selector: 'app-menu-component',
  imports: [ProductoCardComponent, MatPaginator, ProductoCardWithAdd],
  templateUrl: './menu-component.html',
  styleUrl: './menu-component.css'
})
export class MenuComponent implements OnInit{

  @Input() rest! : RestauranteResponse
  private mService : MenuService = inject(MenuService)
  private pService : ProductoService = inject(ProductoService)
  private router : Router = inject(Router)
  private snackBar : MatSnackBar = inject(MatSnackBar)

  isLoading = false
  sizeProductos = 10;
  pageProductos = 0;
  lengthProductos = 0;

  menu? : MenuResponse
  arrProductos? : ProductoResponse[]

  ngOnInit(): void {
    if(!this.rest){
      this.router.navigate(['/'])
    }else{
      this.buscarMenu()
    }
  }

  buscarMenu(){
    this.mService.getByRestauranteId(this.rest.id).subscribe({
      next:(data)=> {this.menu = data,
        console.log("MIAW ", this.menu)
        this.cargarProductos()
      },
      error:(e)=> {console.log(e),
          this.router.navigate(['/'])
      }
    })
  }

  cargarProductos(){
    if(!this.menu){
              console.log("Holaaaa El menu leido es  ", this.menu)

      this.isLoading=false
      return
    }

    this.pService.getAllByIdMenu(this.menu.id,this.pageProductos,this.sizeProductos).subscribe({
      next:(data)=> {
        console.log("Los productos leido es  ", data)
        this.arrProductos = data.content;
        this.lengthProductos = data.totalElements;
        this.isLoading = false;},
      error: ()=>{
        this.mostrarError('❌ Error al cargar los productos');
        this.isLoading = false;
      }
    })
  }

  onPageChangeProductos(event: PageEvent): void {
    this.pageProductos = event.pageIndex;
    this.sizeProductos = event.pageSize;
    this.cargarProductos();
  }

  private mostrarError(mensaje: string): void {
    this.snackBar.open(mensaje, 'Cerrar', { duration: 3000 });
  }
}
