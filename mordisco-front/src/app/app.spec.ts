import { signal, WritableSignal } from '@angular/core';
import { ComponentFixture, TestBed, fakeAsync, flushMicrotasks } from '@angular/core/testing';
import { NavigationEnd, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { AuthService } from './shared/services/auth-service';
import { NotificacionService } from './shared/services/notificacion/notificacion-service';
import { App } from './app';

describe('App notification authentication lifecycle', () => {
  let fixture: ComponentFixture<App>;
  let authService: {
    currentUser: WritableSignal<{ userId: number; role: string } | null>;
    isAuthenticated: WritableSignal<boolean>;
  };
  let notificationService: jasmine.SpyObj<NotificacionService>;

  beforeEach(async () => {
    authService = {
      currentUser: signal({ userId: 12, role: 'ROLE_CLIENTE' }),
      isAuthenticated: signal(true)
    };
    notificationService = jasmine.createSpyObj<NotificacionService>('NotificacionService', ['conectar', 'desconectar']);
    const events = new Subject<NavigationEnd>();

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: NotificacionService, useValue: notificationService },
        { provide: Router, useValue: { url: '/', events } }
      ]
    })
      .overrideComponent(App, { set: { imports: [], template: '' } })
      .compileComponents();
  });

  it('connects while authenticated and immediately deactivates notifications after logout', fakeAsync(() => {
    fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    flushMicrotasks();

    expect(notificationService.conectar).toHaveBeenCalledWith(12, 'ROLE_CLIENTE');

    authService.currentUser.set(null);
    authService.isAuthenticated.set(false);
    fixture.detectChanges();
    flushMicrotasks();

    expect(notificationService.desconectar).toHaveBeenCalled();
  }));
});
