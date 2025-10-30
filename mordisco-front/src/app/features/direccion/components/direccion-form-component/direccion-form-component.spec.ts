import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DireccionFormComponent } from './direccion-form-component';

describe('DireccionFormComponent', () => {
  let component: DireccionFormComponent;
  let fixture: ComponentFixture<DireccionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DireccionFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DireccionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
