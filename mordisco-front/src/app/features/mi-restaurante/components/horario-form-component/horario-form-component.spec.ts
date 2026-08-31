import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { HorarioFormComponent } from './horario-form-component';

describe('HorarioFormComponent', () => {
  let component: HorarioFormComponent;
  let fixture: ComponentFixture<HorarioFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HorarioFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
