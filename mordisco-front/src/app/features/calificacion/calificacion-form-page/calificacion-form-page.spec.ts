import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { NEVER } from 'rxjs';

import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { CalificacionFormPage } from './calificacion-form-page';

describe('CalificacionFormPage', () => {
  let component: CalificacionFormPage;
  let fixture: ComponentFixture<CalificacionFormPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionFormPage],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ id: '1', var: 'pedido' }) }
          }
        },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
        { provide: PedidoService, useValue: { getById: () => NEVER } },
        { provide: CalificacionService, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CalificacionFormPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
