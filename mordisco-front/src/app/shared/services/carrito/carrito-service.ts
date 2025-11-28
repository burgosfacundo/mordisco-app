import { Injectable, signal, computed, effect } from '@angular/core';
import { ItemCarrito } from '../../models/carrito/item-carrito';
import { CarritoResumen } from '../../models/carrito/carrito-resumen';

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private readonly STORAGE_KEY = 'mordisco_carrito'
  
  // Estado reactivo
  private _items = signal<ItemCarrito[]>([])
  
  // Getters públicos (readonly)
  items = this._items.asReadonly()
  
  // Computed values
  cantidadTotal = computed(() => 
    this._items().reduce((sum, item) => sum + item.cantidad, 0)
  )
  
  subtotal = computed(() => 
    this._items().reduce((sum, item) => sum + (item.precio * item.cantidad), 0)
  )
  
  total = computed(() => 
    this.subtotal()
  )
  
  restauranteActual = computed(() => {
    const items = this._items()
    if (items.length === 0) return null
    
    return {
      id: items[0].restauranteId,
      nombre: items[0].restauranteNombre
    }
  })
  
  tieneItems = computed(() => this._items().length > 0)
  
  resumen = computed((): CarritoResumen => ({
    items: this._items(),
    subtotal: this.subtotal(),
    costoEnvio: 0,
    total: this.total(),
    cantidadItems: this.cantidadTotal(),
    restauranteId: this.restauranteActual()?.id ?? null,
    restauranteNombre: this.restauranteActual()?.nombre ?? null
  }))

  constructor() {
    this.cargarDesdeStorage()
    // Guardar automáticamente en localStorage cuando cambie el carrito
    effect(() => {
      this.guardarEnStorage()
    })
  }

  agregarProducto(item: Omit<ItemCarrito, 'cantidad'>): void {
    const items = this._items()
    
    // Validar que sea del mismo restaurante
    if (items.length > 0 && items[0].restauranteId !== item.restauranteId) {
      throw new Error(
        `Solo puedes agregar productos de ${items[0].restauranteNombre}. ` +
        `Vacía el carrito primero para pedir de otro restaurante.`
      )
    }
    
    // Verificar si el producto ya existe
    const existente = items.find(i => i.productoId === item.productoId)
    
    if (existente) {
      this.incrementarCantidad(existente.productoId)
    } else {
      this._items.update(arr => [...arr, { ...item, cantidad: 1 }])
    }
  }

  incrementarCantidad(productoId: number): void {
    this._items.update(arr => 
      arr.map(item => 
        item.productoId === productoId 
          ? { ...item, cantidad: item.cantidad + 1 }
          : item
      )
    )
  }

  decrementarCantidad(productoId: number): void {
    this._items.update(arr => {
      const item = arr.find(i => i.productoId === productoId)
      
      if (item && item.cantidad === 1) {
        return arr.filter(i => i.productoId !== productoId)
      }
      
      return arr.map(i => 
        i.productoId === productoId 
          ? { ...i, cantidad: i.cantidad - 1 }
          : i
      )
    })
  }

  actualizarCantidad(productoId: number, cantidad: number): void {
    if (cantidad < 1) {
      this.eliminarProducto(productoId)
      return
    }
    
    this._items.update(arr => 
      arr.map(item => 
        item.productoId === productoId 
          ? { ...item, cantidad }
          : item
      )
    )
  }

  eliminarProducto(productoId: number): void {
    this._items.update(arr => arr.filter(item => item.productoId !== productoId))
  }

  vaciarCarrito(): void {
    this._items.set([])
  }

  obtenerProducto(productoId: number): ItemCarrito | undefined {
    return this._items().find(item => item.productoId === productoId)
  }

  contieneProducto(productoId: number): boolean {
    return this._items().some(item => item.productoId === productoId)
  }

  obtenerCantidad(productoId: number): number {
    const item = this.obtenerProducto(productoId)
    return item?.cantidad ?? 0
  }

  validarDisponibilidad(): { valido: boolean; productosNoDisponibles: string[] } {
    const noDisponibles = this._items()
      .filter(item => !item.disponible)
      .map(item => item.nombre)
    
    return {
      valido: noDisponibles.length === 0,
      productosNoDisponibles: noDisponibles
    }
  }

  private cargarDesdeStorage(): void {
    const data = localStorage.getItem(this.STORAGE_KEY)
    
    if (data) {
      try {
        const items: ItemCarrito[] = JSON.parse(data)
        
        // Validar que los datos sean correctos
        if (Array.isArray(items)) {
          this._items.set(items)
        }
      } catch (error) {
        console.error('Error al cargar carrito desde localStorage:', error)
        localStorage.removeItem(this.STORAGE_KEY)
      }
    }
  }

  private guardarEnStorage(): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this._items()))
    } catch (error) {
      console.error('Error al guardar carrito en localStorage:', error)
    }
  }
}