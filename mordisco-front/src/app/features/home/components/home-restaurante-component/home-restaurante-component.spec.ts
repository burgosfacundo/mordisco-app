import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { HomeRestauranteComponent } from './home-restaurante-component';

describe('HomeRestaurante', () => {
  let component: HomeRestauranteComponent;
  let fixture: ComponentFixture<HomeRestauranteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeRestauranteComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeRestauranteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
