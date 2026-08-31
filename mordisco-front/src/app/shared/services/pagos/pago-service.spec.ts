import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { PagoService } from './pago-service';

describe('PagoService', () => {
  let service: PagoService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(PagoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
