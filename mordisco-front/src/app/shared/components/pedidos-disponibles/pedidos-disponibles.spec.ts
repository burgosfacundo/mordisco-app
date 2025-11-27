import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidosDisponibles } from './pedidos-disponibles';

describe('PedidosDisponibles', () => {
  let component: PedidosDisponibles;
  let fixture: ComponentFixture<PedidosDisponibles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidosDisponibles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidosDisponibles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
