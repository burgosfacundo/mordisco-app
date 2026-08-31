import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { CalificacionPage } from './calificacion-page';

describe('CalificacionPage', () => {
  let component: CalificacionPage;
  let fixture: ComponentFixture<CalificacionPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
