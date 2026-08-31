import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { NEVER } from 'rxjs';

import { ToastService } from '../../../core/services/toast-service';
import { PedidoService } from '../../services/pedido/pedido-service';
import { PedidoActivoComponent } from './pedido-activo-component';

describe('PedidoActivoComponent', () => {
  let component: PedidoActivoComponent;
  let fixture: ComponentFixture<PedidoActivoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoActivoComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } }
        },
        { provide: PedidoService, useValue: { getById: () => NEVER } },
        {
          provide: ToastService,
          useValue: {
            info: jasmine.createSpy('info'),
            success: jasmine.createSpy('success'),
            error: jasmine.createSpy('error')
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoActivoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
