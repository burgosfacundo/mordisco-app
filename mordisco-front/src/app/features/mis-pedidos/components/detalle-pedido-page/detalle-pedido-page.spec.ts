import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { NEVER } from 'rxjs';

import { AuthService } from '../../../../shared/services/auth-service';
import { ConfiguracionSistemaService } from '../../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { DetallePedidoPage } from './detalle-pedido-page';

describe('DetallePedidoPage', () => {
  let component: DetallePedidoPage;
  let fixture: ComponentFixture<DetallePedidoPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetallePedidoPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              url: [{ path: 'pedidos' }, { path: '1' }],
              paramMap: convertToParamMap({ id: '1' })
            }
          }
        },
        { provide: PedidoService, useValue: { getById: () => NEVER } },
        { provide: ConfiguracionSistemaService, useValue: { getConfiguracionGeneral: () => NEVER } },
        { provide: AuthService, useValue: { currentUser: () => null } },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetallePedidoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
