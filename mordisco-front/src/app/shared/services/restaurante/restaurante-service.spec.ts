import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { RestauranteService } from './restaurante-service';

describe('RestauranteService', () => {
  let service: RestauranteService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(RestauranteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
