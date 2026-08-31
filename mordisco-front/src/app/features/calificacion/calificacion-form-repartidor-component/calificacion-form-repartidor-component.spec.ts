import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { CalificacionFormRepartidorComponent } from './calificacion-form-repartidor-component';

describe('CalificacionFormRepartidorComponent', () => {
  let component: CalificacionFormRepartidorComponent;
  let fixture: ComponentFixture<CalificacionFormRepartidorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionFormRepartidorComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionFormRepartidorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
