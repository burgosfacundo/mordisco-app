import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { HomeRepartidorComponent } from './home-repartidor-component';

describe('HomeRepartidorComponent', () => {
  let component: HomeRepartidorComponent;
  let fixture: ComponentFixture<HomeRepartidorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeRepartidorComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeRepartidorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
