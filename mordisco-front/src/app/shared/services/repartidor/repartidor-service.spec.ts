import { TestBed } from '@angular/core/testing';

import { RepartidorService } from './repartidor-service';

describe('RepartidorService', () => {
  let service: RepartidorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RepartidorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
