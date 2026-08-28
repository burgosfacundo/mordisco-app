import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Client, StompSubscription } from '@stomp/stompjs';
import { AuthService } from '../auth-service';
import { NotificacionService } from './notificacion-service';

describe('NotificacionService websocket authorization', () => {
  let service: NotificacionService;
  let authService: jasmine.SpyObj<AuthService>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    localStorage.clear();
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['getAccessToken']);
    snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    spyOn(Client.prototype, 'activate').and.stub();
    spyOn(Client.prototype, 'deactivate').and.returnValue(Promise.resolve());

    TestBed.configureTestingModule({
      providers: [
        NotificacionService,
        { provide: AuthService, useValue: authService },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    });
    service = TestBed.inject(NotificacionService);
  });

  afterEach(() => TestBed.resetTestingModule());

  function client(): Client {
    return (service as any).client as Client;
  }

  async function prepareConnection(): Promise<Client> {
    const stompClient = client();
    await stompClient.beforeConnect(stompClient);
    return stompClient;
  }

  function subscribeClient(stompClient: Client): jasmine.Spy {
    return spyOn(stompClient, 'subscribe').and.returnValue({
      unsubscribe: jasmine.createSpy('unsubscribe')
    } as unknown as StompSubscription);
  }

  it('uses the current token on the initial connection', async () => {
    authService.getAccessToken.and.returnValue('initial-token');

    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();

    expect(stompClient.connectHeaders).toEqual({ Authorization: 'Bearer initial-token' });
  });

  it('rebuilds headers from a refreshed token on reconnect without capturing the stale token', async () => {
    authService.getAccessToken.and.returnValues('preflight-token', 'initial-token', 'refreshed-token');

    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();
    await stompClient.beforeConnect(stompClient);

    expect(stompClient.connectHeaders).toEqual({ Authorization: 'Bearer refreshed-token' });
    expect(authService.getAccessToken).toHaveBeenCalledTimes(3);
  });

  it('subscribes clients through the private user destination', async () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();
    const subscribe = subscribeClient(stompClient);

    stompClient.onConnect({} as any);

    expect(subscribe.calls.argsFor(0)[0]).toBe('/user/queue/notificaciones');
  });

  it('subscribes the shared courier topic only for REPARTIDOR', async () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_REPARTIDOR');
    const stompClient = await prepareConnection();
    const subscribe = subscribeClient(stompClient);

    stompClient.onConnect({} as any);

    expect(subscribe.calls.allArgs().map(([destination]) => destination)).toEqual([
      '/user/queue/notificaciones',
      '/topic/repartidores'
    ]);
  });

  it('does not subscribe non-courier roles to the shared courier topic', async () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();
    const subscribe = subscribeClient(stompClient);

    stompClient.onConnect({} as any);

    expect(subscribe.calls.allArgs().map(([destination]) => destination)).not.toContain('/topic/repartidores');
  });

  it('deactivates the client when logout clears authentication', () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_CLIENTE');

    service.desconectar();

    expect(Client.prototype.deactivate).toHaveBeenCalled();
    expect((service as any).client).toBeUndefined();
  });

  it('does not connect and clears an existing connection when no token is available', () => {
    authService.getAccessToken.and.returnValue(null);

    service.conectar(12, 'ROLE_CLIENTE');

    expect(Client.prototype.activate).not.toHaveBeenCalled();
    expect((service as any).client).toBeUndefined();
  });

  it('deactivates after a STOMP authentication failure to stop reconnect attempts', async () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();

    stompClient.onStompError({ headers: { message: 'Authentication failed' } } as any);

    expect(Client.prototype.deactivate).toHaveBeenCalled();
    expect((service as any).client).toBeUndefined();
  });

  it('preserves notification payload, storage, unread state, and toast configuration', async () => {
    authService.getAccessToken.and.returnValue('token');
    service.conectar(12, 'ROLE_CLIENTE');
    const stompClient = await prepareConnection();
    const subscribe = subscribeClient(stompClient);
    stompClient.onConnect({} as any);

    subscribe.calls.argsFor(0)[1]({
      body: JSON.stringify({
        tipo: 'NUEVO_PEDIDO',
        mensaje: 'Nuevo pedido recibido',
        pedidoId: 44,
        estado: 'PENDIENTE'
      })
    });

    expect(service.notificaciones()[0]).toEqual(jasmine.objectContaining({
      tipo: 'NUEVO_PEDIDO',
      mensaje: 'Nuevo pedido recibido',
      pedidoId: 44,
      estado: 'PENDIENTE',
      leida: false
    }));
    expect(service.noLeidas()).toBe(1);
    expect(JSON.parse(localStorage.getItem('mordisco_notificaciones') ?? '[]')[0]).toEqual(
      jasmine.objectContaining({ tipo: 'NUEVO_PEDIDO', mensaje: 'Nuevo pedido recibido', pedidoId: 44, estado: 'PENDIENTE' })
    );
    expect(snackBar.open).toHaveBeenCalledWith('Nuevo pedido recibido', 'Ver', {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['snackbar-success']
    });
  });
});
