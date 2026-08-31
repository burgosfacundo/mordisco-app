import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { PedidoService } from './pedido-service';


describe('PedidoService', () => {
  let service: PedidoService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(PedidoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
