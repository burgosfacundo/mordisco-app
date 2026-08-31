import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { HorarioFormPage } from './horario-form-page';

describe('HorarioFormPage', () => {
  let component: HorarioFormPage;
  let fixture: ComponentFixture<HorarioFormPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioFormPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HorarioFormPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
