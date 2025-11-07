import { Component, inject, Input } from '@angular/core';
import ProductoCard from '../../models/producto/producto-card';

@Component({
  selector: 'app-producto-card-with-add',
  imports: [],
  templateUrl: './producto-card-with-add.html',
  styleUrl: './producto-card-with-add.css'
})
export class ProductoCardWithAdd {
  @Input() producto? : ProductoCard

  cantidad : number = 0
  private cService : CarritoService = inject(CarritoService)

  incrementar(){
    this.cService().agregarProducto(this.producto).subscribe({

    })
    this.cantidad++
  }

  decrementar(){
    this.cService().quitarProducto(this.producto?.id).subscribe({
      
    })
    this.cantidad--
  }

}
