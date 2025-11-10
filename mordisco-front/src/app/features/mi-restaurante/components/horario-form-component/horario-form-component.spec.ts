import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HorarioFormComponent } from './horario-form-component';

describe('HorarioFormComponent', () => {
  let component: HorarioFormComponent;
  let fixture: ComponentFixture<HorarioFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioFormComponent]
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
