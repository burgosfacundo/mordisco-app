import { computed, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { NotificacionService } from '../../services/notificacion/notificacion-service';
import { NotificacionesDropdownComponent } from './notificaciones-dropdown';

describe('NotificacionesDropdownComponent notification compatibility', () => {
  let fixture: ComponentFixture<NotificacionesDropdownComponent>;

  beforeEach(async () => {
    const notifications = signal([{
      tipo: 'NUEVO_PEDIDO',
      mensaje: 'Nuevo pedido recibido',
      pedidoId: 44,
      estado: 'PENDIENTE',
      timestamp: new Date('2025-01-01T12:00:00Z'),
      leida: false
    }]);
    const notificationService = {
      notificaciones: notifications.asReadonly(),
      noLeidas: computed(() => notifications().filter(notification => !notification.leida).length),
      hayNotificaciones: computed(() => notifications().length > 0),
      conectado: signal(true).asReadonly(),
      marcarComoLeida: jasmine.createSpy('marcarComoLeida'),
      marcarTodasComoLeidas: jasmine.createSpy('marcarTodasComoLeidas'),
      limpiarTodas: jasmine.createSpy('limpiarTodas'),
      obtenerIconoPorTipo: jasmine.createSpy('obtenerIconoPorTipo').and.returnValue('shopping_bag'),
      obtenerColorPorTipo: jasmine.createSpy('obtenerColorPorTipo').and.returnValue('text-green-600')
    };

    await TestBed.configureTestingModule({
      imports: [NotificacionesDropdownComponent],
      providers: [
        { provide: NotificacionService, useValue: notificationService },
        { provide: AuthService, useValue: { currentUser: () => null } },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
        { provide: MatDialog, useValue: { open: jasmine.createSpy('open') } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificacionesDropdownComponent);
    fixture.detectChanges();
  });

  it('continues to display notification message, unread badge, and connection state', async () => {
    const element = fixture.nativeElement as HTMLElement;
    const trigger = element.querySelector('button');

    expect(trigger).toBeTruthy();
    trigger!.click();
    fixture.detectChanges();
    await fixture.whenStable();

    const menuContent = document.querySelector('.mat-mdc-menu-panel')?.textContent;

    expect(menuContent).toContain('Nuevo pedido recibido');
    expect(menuContent).toContain('Conectado');
    expect(element.querySelector('.mat-badge-content')?.textContent).toContain('1');
  });
});
