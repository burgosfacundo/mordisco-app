import { TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { CheckoutPage } from './checkout-page';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { DireccionService } from '../../../direccion/services/direccion-service';
import { CarritoService } from '../../../../shared/services/carrito/carrito-service';
import { ToastService } from '../../../../core/services/toast-service';
import { GeolocationService } from '../../../../shared/services/geolocation/geolocation-service';
import { ConfiguracionSistemaService } from '../../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { httpErrorInterceptor } from '../../../../core/interceptors/http-error-interceptor';
import { ErrorHandlerService } from '../../../../core/services/error-handler-service';
import { environment } from '../../../../../environments/environment';

describe('CheckoutPage payment fallback', () => {
  let component: CheckoutPage;
  let carrito: { items: ReturnType<typeof signal>; resumen: jasmine.Spy; tieneItems: jasmine.Spy; vaciarCarrito: jasmine.Spy };
  let pedido: jasmine.SpyObj<PedidoService>;
  let router: jasmine.SpyObj<Router>;
  let toast: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    carrito = {
      items: signal([{ productoId: 10, cantidad: 1, precio: 1200, precioConDescuento: null, nombre: 'Empanadas', imagen: { url: '' }, restauranteId: 4 } as any]),
      resumen: jasmine.createSpy().and.returnValue({ restauranteId: 4 }),
      tieneItems: jasmine.createSpy().and.returnValue(true),
      vaciarCarrito: jasmine.createSpy()
    };
    pedido = jasmine.createSpyObj<PedidoService>('PedidoService', ['crearPedido']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    toast = jasmine.createSpyObj<ToastService>('ToastService', ['success', 'error']);

    await TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: ToastService, useValue: toast },
        { provide: CarritoService, useValue: carrito },
        { provide: PedidoService, useValue: pedido },
        { provide: AuthService, useValue: { currentUser: () => ({ userId: 7 }) } },
        { provide: DireccionService, useValue: { getMisDirecciones: () => of([]) } },
        { provide: GeolocationService, useValue: {} },
        { provide: ConfiguracionSistemaService, useValue: { getConfiguracionGeneral: () => of({ montoMinimoPedido: 0 }) } },
        { provide: RestauranteService, useValue: {} }
      ]
    });

    component = TestBed.runInInjectionContext(() => new CheckoutPage());
    component.ngOnInit();
    component.checkoutForm.patchValue({ tipoEntrega: 'RETIRO_POR_LOCAL', metodoPago: 'MERCADO_PAGO' });
  });

  it('keeps the cart and offers cash when Mercado Pago is unavailable', () => {
    pedido.crearPedido.and.returnValue(throwError(() => ({ status: 503 })) as any);

    component.confirmarPedido();

    expect(carrito.vaciarCarrito).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.isProcessing()).toBeFalse();
    expect(toast.error).toHaveBeenCalledWith(jasmine.stringMatching(/Mercado Pago.*efectivo/i));
  });

  it('keeps the cart and stays on checkout when Mercado Pago returns no redirect URL', () => {
    pedido.crearPedido.and.returnValue(of({ initPoint: null, sandboxInitPoint: null }) as any);

    component.confirmarPedido();

    expect(carrito.vaciarCarrito).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledWith(jasmine.stringMatching(/Mercado Pago.*efectivo/i));
  });

  it('redirects to Mercado Pago after clearing the cart', () => {
    pedido.crearPedido.and.returnValue(of({
      initPoint: 'https://pay.example/checkout',
      sandboxInitPoint: 'https://sandbox.example/checkout'
    }) as any);
    const redirect = spyOn(component as any, 'redirectToMercadoPago');

    component.confirmarPedido();

    expect(redirect).toHaveBeenCalledOnceWith('https://sandbox.example/checkout');
    expect(carrito.vaciarCarrito).toHaveBeenCalled();
    expect(component.isProcessing()).toBeFalse();
  });

  it('preserves successful cash checkout behavior', () => {
    component.checkoutForm.patchValue({ metodoPago: 'EFECTIVO' });
    pedido.crearPedido.and.returnValue(of({}) as any);

    component.confirmarPedido();

    expect(carrito.vaciarCarrito).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/cliente/pedidos']);
  });
});

describe('CheckoutPage HTTP error integration', () => {
  let component: CheckoutPage;
  let carrito: { items: ReturnType<typeof signal>; resumen: jasmine.Spy; tieneItems: jasmine.Spy; vaciarCarrito: jasmine.Spy };
  let router: jasmine.SpyObj<Router>;
  let toast: jasmine.SpyObj<ToastService>;
  let errorHandler: jasmine.SpyObj<ErrorHandlerService>;
  let http: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    carrito = {
      items: signal([{ productoId: 10, cantidad: 1, precio: 1200, precioConDescuento: null, nombre: 'Empanadas', imagen: { url: '' }, restauranteId: 4 } as any]),
      resumen: jasmine.createSpy().and.returnValue({ restauranteId: 4 }),
      tieneItems: jasmine.createSpy().and.returnValue(true),
      vaciarCarrito: jasmine.createSpy()
    };
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    toast = jasmine.createSpyObj<ToastService>('ToastService', ['success', 'error', 'showFromError']);
    errorHandler = jasmine.createSpyObj<ErrorHandlerService>('ErrorHandlerService', ['handleHttpError', 'handle']);
    errorHandler.handleHttpError.and.callFake((error: HttpErrorResponse) => ({
      type: 'http',
      severity: 'error',
      userMessage: 'El servicio no está disponible en este momento. Intenta más tarde.',
      technicalMessage: `HTTP ${error.status}`,
      showToUser: true,
      statusCode: error.status
    }));
    errorHandler.handle.and.callFake((error: HttpErrorResponse) =>
      throwError(() => ({ type: 'http', statusCode: error.status }))
    );

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([httpErrorInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
        { provide: ToastService, useValue: toast },
        { provide: ErrorHandlerService, useValue: errorHandler },
        { provide: CarritoService, useValue: carrito },
        { provide: AuthService, useValue: { currentUser: () => ({ userId: 7 }) } },
        { provide: DireccionService, useValue: { getMisDirecciones: () => of([]) } },
        { provide: GeolocationService, useValue: {} },
        { provide: ConfiguracionSistemaService, useValue: { getConfiguracionGeneral: () => of({ montoMinimoPedido: 0 }) } },
        { provide: RestauranteService, useValue: {} }
      ]
    });
    http = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    component = TestBed.runInInjectionContext(() => new CheckoutPage());
    component.ngOnInit();
    component.checkoutForm.patchValue({ tipoEntrega: 'RETIRO_POR_LOCAL', metodoPago: 'MERCADO_PAGO' });
  });

  afterEach(() => httpTestingController.verify());

  it('delivers the actual unavailable response to checkout and shows only its specific recovery toast', () => {
    component.confirmarPedido();
    const request = httpTestingController.expectOne(`${environment.apiUrl}/pedidos/save`);

    request.flush({ message: 'internal provider detail' }, { status: 503, statusText: 'Service Unavailable' });

    expect(toast.showFromError).not.toHaveBeenCalled();
    expect(toast.error).toHaveBeenCalledOnceWith(jasmine.stringMatching(/Mercado Pago no está disponible.*efectivo/i));
    expect(carrito.vaciarCarrito).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.isProcessing()).toBeFalse();
  });

  it('retains centralized safe server-error handling for unrelated requests', () => {
    let observedError: unknown;
    http.get(`${environment.apiUrl}/another-page`).subscribe({ error: error => observedError = error });
    const request = httpTestingController.expectOne(`${environment.apiUrl}/another-page`);

    request.flush({ message: 'internal provider detail' }, { status: 503, statusText: 'Service Unavailable' });

    expect(errorHandler.handleHttpError).toHaveBeenCalledOnceWith(jasmine.any(HttpErrorResponse));
    expect(toast.showFromError).toHaveBeenCalledOnceWith(jasmine.objectContaining({
      userMessage: 'El servicio no está disponible en este momento. Intenta más tarde.',
      statusCode: 503
    }));
    expect(observedError).toEqual({ type: 'http', statusCode: 503 });
    expect(toast.error).not.toHaveBeenCalled();
  });
});
