import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestauranteDetallePage } from './detalle-restaurante';

describe('RestauranteDetallePage', () => {
  let component: RestauranteDetallePage;
  let fixture: ComponentFixture<RestauranteDetallePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestauranteDetallePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestauranteDetallePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
