import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { CalificacionFormPedidoComponent } from './calificacion-form-pedido-component';

describe('CalificacionFormPedidoComponent', () => {
  let component: CalificacionFormPedidoComponent;
  let fixture: ComponentFixture<CalificacionFormPedidoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionFormPedidoComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
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
