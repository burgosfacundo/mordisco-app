import { Injectable, signal, computed, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notificacion } from '../../models/notificacion/notificacion-dto';
import { TipoNotificacion } from '../../models/notificacion/tipo-notificacion';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private client?: Client;
  private subscription?: StompSubscription;
  private snackBar = inject(MatSnackBar);
  
  private readonly STORAGE_KEY = 'mordisco_notificaciones';
  private readonly MAX_NOTIFICACIONES = 50;
  
  // Estado reactivo
  private _notificaciones = signal<Notificacion[]>([]);
  private _conectado = signal(false);
  
  // Getters públicos
  notificaciones = this._notificaciones.asReadonly();
  conectado = this._conectado.asReadonly();
  
  // Computed
  noLeidas = computed(() => 
    this._notificaciones().filter(n => !n.leida).length
  );
  
  hayNotificaciones = computed(() => 
    this._notificaciones().length > 0
  );

  constructor() {
    this.cargarNotificacionesDesdeStorage();
  }

  conectar(userId: number, role: string): void {
    if (this.client?.connected) {
      return;
    }

    const wsUrl = environment.apiUrl.replace(/^http/, 'ws') + '/ws';

    this.client = new Client({
       webSocketFactory: () => new WebSocket(wsUrl),
      
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      onConnect: () => {
        this._conectado.set(true);
        
        const topic = this.obtenerTopic(userId, role);
        
        this.subscription = this.client?.subscribe(topic, (message) => {
          try {
            const notif = JSON.parse(message.body);
            this.procesarNotificacion(notif);
          } catch (error) {
            console.error('❌ Error al parsear notificación:', error);
          }
        });
      },
      
      onDisconnect: () => {
        this._conectado.set(false);
      },
      
      onStompError: (frame) => {
        this._conectado.set(false);
      },
      
      onWebSocketError: (event) => {
        this._conectado.set(false);
      },

      onWebSocketClose: (event) => {
        this._conectado.set(false);
      }
    });

    this.client.activate();
  }

  desconectar(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = undefined;
    }
    
    if (this.client) {
      this.client.deactivate();
      this.client = undefined;
    }
    
    this._conectado.set(false);
  }

  private obtenerTopic(userId: number, role: string): string {
    const roleNormalizado = role.toUpperCase().replace('ROLE_', '');
    
    let topic: string;
    
    switch (roleNormalizado) {
      case 'RESTAURANTE':
        topic = `/topic/restaurante/${userId}`;
        break;
      case 'CLIENTE':
        topic = `/topic/cliente/${userId}`;
        break;
      case 'REPARTIDOR':
        topic = `/topic/repartidor/${userId}`;
        break;
      case 'ADMIN':
        topic = `/topic/admin/${userId}`;
        break;
      default:
        topic = `/topic/usuario/${userId}`;
    }
    
    return topic;
  }

  private procesarNotificacion(data: any): void {
    const notif: Notificacion = {
      tipo: data.tipo as TipoNotificacion,
      mensaje: data.mensaje,
      pedidoId: data.pedidoId,
      estado: data.estado,
      timestamp: new Date(),
      leida: false
    };

    this.agregarNotificacion(notif);
    this.mostrarNotificacionNativa(notif);
    this.mostrarToast(notif);
  }

  private agregarNotificacion(notif: Notificacion): void {
    this._notificaciones.update(arr => {
      const nuevasNotif = [notif, ...arr].slice(0, this.MAX_NOTIFICACIONES);
      return nuevasNotif;
    });
    this.guardarEnStorage();
  }

  marcarComoLeida(index: number): void {
    this._notificaciones.update(arr => 
      arr.map((n, i) => i === index ? { ...n, leida: true } : n)
    );
    this.guardarEnStorage();
  }

  marcarTodasComoLeidas(): void {
    this._notificaciones.update(arr => 
      arr.map(n => ({ ...n, leida: true }))
    );
    this.guardarEnStorage();
  }

  eliminarNotificacion(index: number): void {
    this._notificaciones.update(arr => arr.filter((_, i) => i !== index));
    this.guardarEnStorage();
  }

  limpiarTodas(): void {
    this._notificaciones.set([]);
    this.guardarEnStorage();
  }

  private mostrarNotificacionNativa(notif: Notificacion): void {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('Mordisco - ' + this.formatearTipo(notif.tipo), {
        body: notif.mensaje,
        icon: '/assets/logo.png',
        badge: '/assets/badge.png',
        tag: `pedido-${notif.pedidoId}`,
        requireInteraction: notif.tipo === TipoNotificacion.NUEVO_PEDIDO
      });
    }
  }

  private mostrarToast(notif: Notificacion): void {
    const config = this.obtenerConfigToast(notif.tipo);
    
    this.snackBar.open(notif.mensaje, 'Ver', {
      duration: config.duration,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: config.panelClass
    });
  }

  obtenerIconoPorTipo(tipo: TipoNotificacion): string {
    switch (tipo) {
      case TipoNotificacion.NUEVO_PEDIDO:
        return 'shopping_bag';
      case TipoNotificacion.CAMBIO_ESTADO_PEDIDO:
        return 'local_shipping';
      case TipoNotificacion.PAGO_CONFIRMADO:
        return 'check_circle';
      case TipoNotificacion.PAGO_RECHAZADO:
        return 'cancel';
      default:
        return 'notifications';
    }
  }

  obtenerColorPorTipo(tipo: TipoNotificacion): string {
    switch (tipo) {
      case TipoNotificacion.NUEVO_PEDIDO:
        return 'text-green-600';
      case TipoNotificacion.CAMBIO_ESTADO_PEDIDO:
        return 'text-blue-600';
      case TipoNotificacion.PAGO_CONFIRMADO:
        return 'text-purple-600';
      case TipoNotificacion.PAGO_RECHAZADO:
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  }

  private formatearTipo(tipo: TipoNotificacion): string {
    switch (tipo) {
      case TipoNotificacion.NUEVO_PEDIDO:
        return 'Nuevo Pedido';
      case TipoNotificacion.CAMBIO_ESTADO_PEDIDO:
        return 'Estado del Pedido';
      case TipoNotificacion.PAGO_CONFIRMADO:
        return 'Pago Confirmado';
      case TipoNotificacion.PAGO_RECHAZADO:
        return 'Pago Rechazado';
      default:
        return 'Notificación';
    }
  }

  private obtenerConfigToast(tipo: TipoNotificacion) {
    switch (tipo) {
      case TipoNotificacion.NUEVO_PEDIDO:
        return { duration: 5000, panelClass: ['snackbar-success'] };
      case TipoNotificacion.PAGO_CONFIRMADO:
        return { duration: 4000, panelClass: ['snackbar-success'] };
      case TipoNotificacion.PAGO_RECHAZADO:
        return { duration: 6000, panelClass: ['snackbar-error'] };
      default:
        return { duration: 3000, panelClass: ['snackbar-info'] };
    }
  }

  private cargarNotificacionesDesdeStorage(): void {
    const data = localStorage.getItem(this.STORAGE_KEY);
    if (data) {
      try {
        const notifs = JSON.parse(data);
        this._notificaciones.set(notifs.map((n: any) => ({
          ...n,
          timestamp: new Date(n.timestamp)
        })));
      } catch (error) {
        console.error('Error al cargar notificaciones:', error);
        localStorage.removeItem(this.STORAGE_KEY);
      }
    }
  }

  private guardarEnStorage(): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this._notificaciones()));
    } catch (error) {
      console.error('Error al guardar notificaciones:', error);
    }
  }
}