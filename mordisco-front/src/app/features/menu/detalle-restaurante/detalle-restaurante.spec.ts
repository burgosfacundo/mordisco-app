import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleRestaurante } from './detalle-restaurante';

describe('DetalleRestaurante', () => {
  let component: DetalleRestaurante;
  let fixture: ComponentFixture<DetalleRestaurante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleRestaurante]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleRestaurante);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
