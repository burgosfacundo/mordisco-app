import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';

import { PagoService } from '../../../../shared/services/pagos/pago-service';
import PagoResponseDTO from '../../../../shared/models/pago/pago-response-dto';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { PagoPendientePage } from './pago-pendiente-page';

describe('PagoPendientePage', () => {
  const pedidoId = 42;
  const pedido = {
    id: pedidoId,
    restaurante: { razonSocial: 'Synthetic Restaurant' }
  } as PedidoResponse;

  let component: PagoPendientePage;
  let fixture: ComponentFixture<PagoPendientePage>;
  let pagoService: jasmine.SpyObj<PagoService>;
  let pedidoService: jasmine.SpyObj<PedidoService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    pagoService = jasmine.createSpyObj<PagoService>('PagoService', ['getPagoByPedidoId']);
    pedidoService = jasmine.createSpyObj<PedidoService>('PedidoService', ['getById']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    pagoService.getPagoByPedidoId.and.returnValue(of({ estado: 'PENDIENTE' } as PagoResponseDTO));
    pedidoService.getById.and.returnValue(of(pedido));

    await TestBed.configureTestingModule({
      imports: [PagoPendientePage],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { queryParamMap: convertToParamMap({ pedido: pedidoId.toString() }) }
          }
        },
        { provide: PagoService, useValue: pagoService },
        { provide: PedidoService, useValue: pedidoService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PagoPendientePage);
    component = fixture.componentInstance;
  });

  afterEach(() => component?.ngOnDestroy());

  it('renders pending only after the authoritative payment state is pending', () => {
    fixture.detectChanges();

    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledWith(pedidoId);
    expect(pedidoService.getById).toHaveBeenCalledWith(pedidoId);
    expect(component.pedido()).toBe(pedido);
    expect(component.isLoading()).toBeFalse();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('performs the immediate lookup and then polls pending status every five seconds', fakeAsync(() => {
    pagoService.getPagoByPedidoId.and.returnValues(
      of({ estado: 'PENDIENTE' } as PagoResponseDTO),
      of({ estado: 'PENDIENTE' } as PagoResponseDTO)
    );

    fixture.detectChanges();
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(1);

    tick(4999);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(1);
    tick(1);

    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(2);
    expect(router.navigate).not.toHaveBeenCalled();
  }));

  it('routes approved and rejected payments away from the pending page', () => {
    pagoService.getPagoByPedidoId.and.returnValue(of({ estado: 'APROBADO' } as PagoResponseDTO));
    fixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledWith(
      ['/cliente/pedidos/pago-exitoso'],
      { queryParams: { pedido: pedidoId } }
    );
    expect(pedidoService.getById).not.toHaveBeenCalled();

    router.navigate.calls.reset();
    pagoService.getPagoByPedidoId.and.returnValue(of({ estado: 'RECHAZADO' } as PagoResponseDTO));
    component.ngOnInit();

    expect(router.navigate).toHaveBeenCalledWith(
      ['/cliente/pedidos/pago-fallido'],
      { queryParams: { pedido: pedidoId } }
    );
    expect(pedidoService.getById).not.toHaveBeenCalled();
  });

  it('routes a terminal polling status and stops further lookups', fakeAsync(() => {
    pagoService.getPagoByPedidoId.and.returnValues(
      of({ estado: 'PENDIENTE' } as PagoResponseDTO),
      of({ estado: 'CANCELADO' } as PagoResponseDTO)
    );

    fixture.detectChanges();
    tick(5000);

    expect(router.navigate).toHaveBeenCalledWith(
      ['/cliente/pedidos/pago-fallido'],
      { queryParams: { pedido: pedidoId } }
    );
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(2);

    tick(10000);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(2);
  }));

  it('stops pending polling after the two-minute timeout', fakeAsync(() => {
    pagoService.getPagoByPedidoId.and.returnValue(of({ estado: 'PENDIENTE' } as PagoResponseDTO));

    fixture.detectChanges();
    tick(120000);
    const callsAtTimeout = pagoService.getPagoByPedidoId.calls.count();

    tick(10000);

    expect(callsAtTimeout).toBeGreaterThan(1);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(callsAtTimeout);
    expect(router.navigate).not.toHaveBeenCalled();
    expect((component as any).pagoPollingSubscription.closed).toBeTrue();
  }));

  it('does not overlap payment lookups and stops them on destroy', fakeAsync(() => {
    const firstLookup = new Subject<PagoResponseDTO>();
    pagoService.getPagoByPedidoId.and.returnValues(
      of({ estado: 'PENDIENTE' } as PagoResponseDTO),
      firstLookup.asObservable(),
      of({ estado: 'PENDIENTE' } as PagoResponseDTO)
    );

    fixture.detectChanges();
    tick(5000);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(2);

    tick(5000);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(2);

    firstLookup.next({ estado: 'PENDIENTE' } as PagoResponseDTO);
    firstLookup.complete();
    tick(5000);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(3);

    component.ngOnDestroy();
    tick(10000);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(3);
  }));

  it('preserves the existing order redirect when payment status cannot be fetched', () => {
    pagoService.getPagoByPedidoId.and.returnValue(throwError(() => new Error('unavailable')));
    fixture.detectChanges();

    expect(component.isLoading()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/cliente/pedidos']);
  });

  it('stops polling and preserves the order redirect when the order lookup fails', () => {
    pedidoService.getById.and.returnValue(throwError(() => new Error('unavailable')));
    fixture.detectChanges();

    expect(component.isLoading()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/cliente/pedidos']);
    expect(pagoService.getPagoByPedidoId).toHaveBeenCalledTimes(1);
  });
});
