import { TestBed } from '@angular/core/testing';

import { ConfiguracionSistemaService } from './configuracion-sistema-service';

describe('ConfiguracionSistemaService', () => {
  let service: ConfiguracionSistemaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfiguracionSistemaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
