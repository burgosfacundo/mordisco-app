import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { HorarioPage } from './horario-page';

describe('HorarioPage', () => {
  let component: HorarioPage;
  let fixture: ComponentFixture<HorarioPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HorarioPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
