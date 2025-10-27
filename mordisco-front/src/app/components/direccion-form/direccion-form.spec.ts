import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DireccionForm } from './direccion-form';

describe('DireccionForm', () => {
  let component: DireccionForm;
  let fixture: ComponentFixture<DireccionForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DireccionForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DireccionForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
