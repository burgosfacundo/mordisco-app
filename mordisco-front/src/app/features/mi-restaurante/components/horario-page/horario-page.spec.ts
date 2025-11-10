import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HorarioPage } from './horario-page';

describe('HorarioPage', () => {
  let component: HorarioPage;
  let fixture: ComponentFixture<HorarioPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioPage]
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
