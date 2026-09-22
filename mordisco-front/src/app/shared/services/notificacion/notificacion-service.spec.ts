import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Client, StompSubscription } from '@stomp/stompjs';
import { of, Subject, throwError } from 'rxjs';
import { AuthService } from '../auth-service';
import { PedidoService } from '../pedido/pedido-service';
import { RestauranteService } from '../restaurante/restaurante-service';
import PedidoResponse from '../../models/pedido/pedido-response';
import PaginationResponse from '../../models/pagination/pagination-response';
import { TipoNotificacion } from '../../models/notificacion/tipo-notificacion';
import { environment } from '../../../../environments/environment';
import { NotificacionService } from './notificacion-service';

describe('NotificacionService websocket authorization and polling fallback', () => {
  let service: NotificacionService;
  let authService: jasmine.SpyObj<AuthService>;
  let pedidoService: jasmine.SpyObj<PedidoService>;
  let restauranteService: jasmine.SpyObj<RestauranteService>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let originalWebsocketEnabled: boolean;

  beforeEach(() => {
    originalWebsocketEnabled = environment.websocketEnabled;
    environment.websocketEnabled = true;
    localStorage.clear();
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['getAccessToken']);
    pedidoService = jasmine.createSpyObj<PedidoService>('PedidoService', [
      'getAllByCliente',
      'findAllByRestaurante_Id'
    ]);
    restauranteService = jasmine.createSpyObj<RestauranteService>('RestauranteService', ['getByUsuario']);
    snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    spyOn(Client.prototype, 'activate').and.stub();
    spyOn(Client.prototype, 'deactivate').and.returnValue(Promise.resolve());

    TestBed.configureTestingModule({
      providers: [
        NotificacionService,
        { provide: AuthService, useValue: authService },
        { provide: PedidoService, useValue: pedidoService },
        { provide: RestauranteService, useValue: restauranteService },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    });
    service = TestBed.inject(NotificacionService);
  });

  afterEach(() => {
    service?.ngOnDestroy();
    environment.websocketEnabled = originalWebsocketEnabled;
    TestBed.resetTestingModule();
  });

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

  function pedido(id: number, estado: string): PedidoResponse {
    return { id, estado } as PedidoResponse;
  }

  function page(content: PedidoResponse[]): PaginationResponse<PedidoResponse> {
    return {
      content,
      page: 0,
      size: 20,
      totalElements: content.length,
      totalPages: content.length ? 1 : 0,
      last: true
    };
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

  it('does not create or activate STOMP when websocket fallback mode is enabled', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');

    service.conectar(12, 'ROLE_ADMIN');
    tick(30000);

    expect(Client.prototype.activate).not.toHaveBeenCalled();
    expect((service as any).client).toBeUndefined();
    expect(pedidoService.getAllByCliente).not.toHaveBeenCalled();
    expect(pedidoService.findAllByRestaurante_Id).not.toHaveBeenCalled();
  }));

  it('polls client orders with a baseline and emits one notification for a later state change', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');
    pedidoService.getAllByCliente.and.returnValues(
      of(page([pedido(44, 'PENDIENTE')])),
      of(page([pedido(44, 'EN_PREPARACION')])),
      of(page([pedido(44, 'EN_PREPARACION')]))
    );

    service.conectar(12, 'ROLE_CLIENTE');

    expect(pedidoService.getAllByCliente).toHaveBeenCalledWith(12, 0, 20);
    expect(service.notificaciones()).toEqual([]);

    tick(14999);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(1);
    tick(1);

    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(2);
    expect(service.notificaciones()).toHaveSize(1);
    expect(service.notificaciones()[0]).toEqual(jasmine.objectContaining({
      pedidoId: 44,
      estado: 'EN_PREPARACION',
      tipo: TipoNotificacion.PEDIDO_EN_PREPARACION
    }));

    tick(15000);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(3);
    expect(service.notificaciones()).toHaveSize(1);
    service.desconectar();
  }));

  it('polls restaurant orders through the owner-resolved restaurant endpoint', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');
    restauranteService.getByUsuario.and.returnValue(of({ id: 9 } as any));
    pedidoService.findAllByRestaurante_Id.and.returnValues(
      of(page([pedido(51, 'PENDIENTE')])),
      of(page([pedido(51, 'LISTO_PARA_ENTREGAR')]))
    );

    service.conectar(18, 'ROLE_RESTAURANTE');

    expect(restauranteService.getByUsuario).toHaveBeenCalledWith(18);
    expect(pedidoService.findAllByRestaurante_Id).toHaveBeenCalledWith(9, 0, 20);
    expect(service.notificaciones()).toEqual([]);

    tick(15000);

    expect(pedidoService.findAllByRestaurante_Id).toHaveBeenCalledTimes(2);
    expect(service.notificaciones()[0]).toEqual(jasmine.objectContaining({
      pedidoId: 51,
      estado: 'LISTO_PARA_ENTREGAR',
      tipo: TipoNotificacion.PEDIDO_LISTO_PARA_ENTREGAR
    }));
    service.desconectar();
  }));

  it('does not poll REPARTIDOR or any other unsupported role', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');

    service.conectar(12, 'ROLE_REPARTIDOR');
    service.conectar(12, 'ROLE_ADMIN');
    tick(60000);

    expect(Client.prototype.activate).not.toHaveBeenCalled();
    expect(pedidoService.getAllByCliente).not.toHaveBeenCalled();
    expect(pedidoService.findAllByRestaurante_Id).not.toHaveBeenCalled();
    expect(restauranteService.getByUsuario).not.toHaveBeenCalled();
  }));

  it('does not overlap polling requests and cleans them up on disconnect and destroy', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');
    const firstRequest = new Subject<PaginationResponse<PedidoResponse>>();
    pedidoService.getAllByCliente.and.returnValues(
      firstRequest.asObservable(),
      of(page([pedido(72, 'EN_PREPARACION')]))
    );

    service.conectar(12, 'ROLE_CLIENTE');
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(1);

    tick(15000);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(1);

    firstRequest.next(page([pedido(72, 'PENDIENTE')]));
    firstRequest.complete();
    tick(15000);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(2);

    service.desconectar();
    tick(30000);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(2);

    service.ngOnDestroy();
    tick(30000);
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(2);
  }));

  it('keeps the last baseline after an error and continues on the existing timer', fakeAsync(() => {
    environment.websocketEnabled = false;
    authService.getAccessToken.and.returnValue('token');
    pedidoService.getAllByCliente.and.returnValues(
      throwError(() => new Error('temporary failure')),
      of(page([pedido(73, 'PENDIENTE')])),
      of(page([pedido(73, 'COMPLETADO')]))
    );

    service.conectar(12, 'ROLE_CLIENTE');
    tick(15000);
    expect(service.notificaciones()).toEqual([]);

    tick(15000);
    expect(service.notificaciones()[0]).toEqual(jasmine.objectContaining({
      pedidoId: 73,
      estado: 'COMPLETADO',
      tipo: TipoNotificacion.PEDIDO_COMPLETADO
    }));
    expect(pedidoService.getAllByCliente).toHaveBeenCalledTimes(3);
    service.desconectar();
  }));

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
