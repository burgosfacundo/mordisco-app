import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { RepartidorService } from './repartidor-service';

describe('RepartidorService', () => {
  let service: RepartidorService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(RepartidorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
