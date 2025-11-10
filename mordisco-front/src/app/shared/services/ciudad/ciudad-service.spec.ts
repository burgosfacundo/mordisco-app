import { TestBed } from '@angular/core/testing';
import { CiudadService } from './ciudad-service';


describe('CiudadService', () => {
  let service: CiudadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CiudadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
