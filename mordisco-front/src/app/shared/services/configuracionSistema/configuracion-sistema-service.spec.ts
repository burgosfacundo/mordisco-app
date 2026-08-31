import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ConfiguracionSistemaService } from './configuracion-sistema-service';

describe('ConfiguracionSistemaService', () => {
  let service: ConfiguracionSistemaService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(ConfiguracionSistemaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
