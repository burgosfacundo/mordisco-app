import { ComponentFixture, TestBed } from '@angular/core/testing';

import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';
import { HorarioCardComponent } from './horario-card-component';

describe('HorarioCardComponent', () => {
  let component: HorarioCardComponent;
  let fixture: ComponentFixture<HorarioCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HorarioCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('horario', {
      id: 1,
      dia: 'MONDAY',
      horaApertura: '09:00:00',
      horaCierre: '18:00:00',
      cruzaMedianoche: false
    } satisfies HorarioAtencionResponse);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
