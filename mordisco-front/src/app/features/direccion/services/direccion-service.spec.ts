import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DireccionService } from './direccion-service';


describe('DireccionService', () => {
  let service: DireccionService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(DireccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
