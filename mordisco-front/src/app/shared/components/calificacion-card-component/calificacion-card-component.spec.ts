import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificacionCardComponent } from './calificacion-card-component';

describe('CalificacionCardComponent', () => {
  let component: CalificacionCardComponent;
  let fixture: ComponentFixture<CalificacionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
