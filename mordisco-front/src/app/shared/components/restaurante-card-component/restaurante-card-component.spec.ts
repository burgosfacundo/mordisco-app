import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RestauranteCardComponent } from './restaurante-card-component';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';

describe('RestauranteCardComponent', () => {
  const restaurante: RestauranteForCard = {
    id: 1,
    razonSocial: 'Restaurante de prueba',
    activo: true,
    logo: { id: 1, url: 'https://example.test/logo.png', nombre: 'Logo de prueba' },
    horariosDeAtencion: [],
    estrellas: 4,
  };
  let component: RestauranteCardComponent;
  let fixture: ComponentFixture<RestauranteCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestauranteCardComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(RestauranteCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('restaurante', restaurante);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(fixture.nativeElement.querySelector('img').alt).toBe(restaurante.logo.nombre);
  });
});
