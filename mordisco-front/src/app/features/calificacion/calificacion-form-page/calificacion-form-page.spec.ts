import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificacionFormPage } from './calificacion-form-page';

describe('CalificacionFormPage', () => {
  let component: CalificacionFormPage;
  let fixture: ComponentFixture<CalificacionFormPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionFormPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionFormPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
