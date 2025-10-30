import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DireccionCardComponent } from './direccion-card-component';

describe('DireccionCardComponent', () => {
  let component: DireccionCardComponent;
  let fixture: ComponentFixture<DireccionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DireccionCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DireccionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
