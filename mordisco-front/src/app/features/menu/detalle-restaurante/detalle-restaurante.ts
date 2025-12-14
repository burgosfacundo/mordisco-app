import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
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
import { ToastService } from '../../../core/services/toast-service';

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
  private toastService = inject(ToastService);
  private restauranteService = inject(RestauranteService);
  private menuService = inject(MenuService);
  private productoService = inject(ProductoService);
  protected carritoService = inject(CarritoService);

  restaurante = signal<RestauranteResponse | null>(null);
  menu = signal<MenuResponse | null>(null);
  productos = signal<ProductoResponse[]>([]);
  isLoading = signal(true);
  adminMode = false

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.adminMode = this.router.url.includes('/admin/restaurante/');
    
    if (!id) {
      this.toastService.success('ID de restaurante inválido');
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
      error: () => {
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
      error: () => {
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
      error: () => {
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
        precioConDescuento: producto.precioConDescuento, // Incluir precio con descuento
        imagen: producto.imagen,
        restauranteId: restaurante.id,
        restauranteNombre: restaurante.razonSocial,
        disponible: producto.disponible
      });

      this.toastService.success(`✅ ${producto.nombre} agregado al carrito`);
    } catch (error: any) {
      // Mostrar mensaje de advertencia con mejor UX
      this.toastService.warning(error.message || 'No se pudo agregar el producto al carrito');
    }
  }

  volver(): void {
    this.router.navigate(['/home']);
  }

  irAlCarrito(): void {
    this.router.navigate(['/cliente/carrito']);
  }

  calificacionPromedio1Dec(): number {
    const prom = this.restaurante()?.estrellas;
    return prom ? Number(prom.toFixed(1)) : 0;
  }
}