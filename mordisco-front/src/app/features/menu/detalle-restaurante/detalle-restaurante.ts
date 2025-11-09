import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import { MenuService } from '../../../shared/services/menu/menu-service';
import { ProductoService } from '../../../shared/services/productos/producto-service';
import { CarritoService } from '../../../shared/services/carrito/carrito-service';
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import MenuResponse from '../../../shared/models/menu/menu-response';
import ProductoResponse from '../../../shared/models/producto/producto-response';
import { ProductoCardWithAdd } from '../../../shared/components/producto-card-with-add/producto-card-with-add';

@Component({
  selector: 'app-restaurante-detalle-page',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTabsModule,
    ProductoCardWithAdd
],
  templateUrl: './detalle-restaurante.html'
})
export class RestauranteDetallePage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private restauranteService = inject(RestauranteService);
  private menuService = inject(MenuService);
  private productoService = inject(ProductoService);
  protected carritoService = inject(CarritoService);

  restaurante = signal<RestauranteResponse | null>(null);
  menu = signal<MenuResponse | null>(null);
  productos = signal<ProductoResponse[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (!id) {
      this.snackBar.open('ID de restaurante inválido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/home']);
      return;
    }

    this.cargarRestaurante(Number(id));
  }

  private cargarRestaurante(id: number): void {
    this.restauranteService.findById(id).subscribe({
      next: (restaurante) => {
        this.restaurante.set(restaurante);
        this.cargarMenu(restaurante.id);
      },
      error: (error) => {
        console.error('Error al cargar restaurante:', error);
        this.snackBar.open('Error al cargar el restaurante', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/home']);
        this.isLoading.set(false);
      }
    });
  }

  private cargarMenu(restauranteId: number): void {
    this.menuService.getByRestauranteId(restauranteId).subscribe({
      next: (menu) => {
        this.menu.set(menu);
        this.cargarProductos(menu.id);
      },
      error: (error) => {
        console.error('Error al cargar menú:', error);
        this.isLoading.set(false);
      }
    });
  }

  private cargarProductos(menuId: number): void {
    this.productoService.getAllByIdMenu(menuId, 0, 100).subscribe({
      next: (response) => {
        const productosDisponibles = response.content.filter(p => p.disponible);
        this.productos.set(productosDisponibles);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
        this.isLoading.set(false);
      }
    });
  }

  agregarAlCarrito(producto: ProductoResponse): void {
    const restaurante = this.restaurante();
    
    if (!restaurante) return;

    try {
      this.carritoService.agregarProducto({
        productoId: producto.id,
        nombre: producto.nombre,
        descripcion: producto.descripcion,
        precio: producto.precio,
        imagen: producto.imagen,
        restauranteId: restaurante.id,
        restauranteNombre: restaurante.razonSocial,
        disponible: producto.disponible
      });

      this.snackBar.open(`✅ ${producto.nombre} agregado al carrito`, 'Cerrar', { 
        duration: 2000 
      });
    } catch (error: any) {
      this.snackBar.open(error.message, 'Cerrar', { duration: 4000 });
    }
  }

  volver(): void {
    this.router.navigate(['/home']);
  }

  irAlCarrito(): void {
    this.router.navigate(['/cliente/carrito']);
  }
}