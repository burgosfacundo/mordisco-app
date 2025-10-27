import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { publicOnlyGuard } from './public-only-guard';

describe('publicOnlyGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => publicOnlyGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
