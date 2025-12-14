import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificacionFormPedidoComponent } from './calificacion-form-pedido-component';

describe('CalificacionFormPedidoComponent', () => {
  let component: CalificacionFormPedidoComponent;
  let fixture: ComponentFixture<CalificacionFormPedidoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionFormPedidoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionFormPedidoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
