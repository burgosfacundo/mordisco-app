import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { NEVER } from 'rxjs';

import { ToastService } from '../../../core/services/toast-service';
import { CarritoService } from '../../../shared/services/carrito/carrito-service';
import { MenuService } from '../../../shared/services/menu/menu-service';
import { ProductoService } from '../../../shared/services/productos/producto-service';
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import { RestauranteDetallePage } from './detalle-restaurante';

describe('RestauranteDetallePage', () => {
  let component: RestauranteDetallePage;
  let fixture: ComponentFixture<RestauranteDetallePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestauranteDetallePage],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } }
        },
        { provide: Router, useValue: { url: '/restaurante/1', navigate: jasmine.createSpy('navigate') } },
        { provide: RestauranteService, useValue: { findById: () => NEVER } },
        { provide: MenuService, useValue: {} },
        { provide: ProductoService, useValue: {} },
        { provide: CarritoService, useValue: {} },
        {
          provide: ToastService,
          useValue: { success: jasmine.createSpy('success'), warning: jasmine.createSpy('warning') }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RestauranteDetallePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
