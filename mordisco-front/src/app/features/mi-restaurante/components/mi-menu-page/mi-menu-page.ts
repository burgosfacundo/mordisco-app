import { Component, inject, OnInit } from '@angular/core';
import { MenuService } from '../../../../shared/services/menu/menu-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import ProductoResponse from '../../../../shared/models/producto/producto-response';
import { ProductoCardComponent } from "../../../../shared/components/producto-card-component/producto-card-component";
import MenuResponse from '../../../../shared/models/menu/menu-response';
import { ProductoService } from '../../../../shared/services/productos/producto-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MenuFormComponent } from "../menu-form-component/menu-form-component";
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-mi-menu-page',
  standalone: true,
  imports: [ProductoCardComponent,MatPaginator , MenuFormComponent,RouterLink ],
  templateUrl: './mi-menu-page.html'
})
export class MiMenuPage implements OnInit {
  private aus = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private mService = inject(MenuService);
  private rService = inject(RestauranteService);
  private pService = inject(ProductoService);

  restaurante?: RestauranteResponse;
  menu?: MenuResponse;
  arrProductos?: ProductoResponse[];

  sizeProductos = 10;
  pageProductos = 0;
  lengthProductos = 0;

  isLoading = true;
  hasRestaurante = false;
  hasMenu = false;

  ngOnInit(): void {
    const userId = this.aus.currentUser()?.userId;
    
    if (!userId) {
      this.snackBar.open('❌ Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.cargarDatos(userId);
  }

  private cargarDatos(userId: number): void {
    this.rService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        if (!restaurante) {
          this.mostrarError('⚠️ Primero debes crear tu restaurante');
          this.router.navigate(['/restaurante']);
          return;
        }

        this.restaurante = restaurante;
        this.hasRestaurante = true;
        
        this.verificarMenu(restaurante.id);
      },
      error: () => {
        this.mostrarError('⚠️ Primero debes crear tu restaurante');
        this.router.navigate(['/restaurante']);
        this.isLoading = false;
      }
    });
  }

  private verificarMenu(restauranteId: number): void {
    this.mService.getByRestauranteId(restauranteId).subscribe({
      next: (menu) => {
        if (!menu?.id) {
          this.hasMenu = false;
          this.isLoading = false;
          return;
        }

        this.menu = menu;
        this.hasMenu = true;
        
        this.cargarProductos();
      },
      error: (error) => {
        if (error.status === 404) {
          this.hasMenu = false;
        }
        this.isLoading = false;
      }
    });
  }

  private cargarProductos(): void {
    if (!this.menu?.id) {
      this.isLoading = false;
      return;
    }

    this.pService.getAllByIdMenu(
      this.menu.id, 
      this.pageProductos, 
      this.sizeProductos
    ).subscribe({
      next: (data) => {
        this.arrProductos = data.content;
        this.lengthProductos = data.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.mostrarError('❌ Error al cargar los productos');
        this.isLoading = false;
      }
    });
  }

  onPageChangeProductos(event: PageEvent): void {
    this.pageProductos = event.pageIndex;
    this.sizeProductos = event.pageSize;
    this.cargarProductos();
  }

  agregarProducto(): void {
    if (!this.menu?.id) {
      this.snackBar.open('⚠️ Error: Menú no disponible', 'Cerrar', { duration: 3000 });
      return;
    }
    this.router.navigate(['/restaurante/menu/producto/nuevo', this.menu.id]);
  }

  editarProducto(producto: ProductoResponse): void {
    this.router.navigate(['/restaurante/menu/producto/editar', producto.id]);
  }

  confirmarEliminacion(id: number): void {
    if (confirm('⚠️ ¿Estás seguro de eliminar este producto?')) {
      this.eliminarProducto(id);
    }
  }

  private eliminarProducto(id: number): void {
    this.pService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('✅ Producto eliminado', 'Cerrar', { duration: 3000 });
        this.cargarProductos();
      },
      error: () => {
        this.mostrarError('❌ Error al eliminar el producto');
      }
    });
  }

  private mostrarError(mensaje: string): void {
    this.snackBar.open(mensaje, 'Cerrar', { duration: 3000 });
  }
}