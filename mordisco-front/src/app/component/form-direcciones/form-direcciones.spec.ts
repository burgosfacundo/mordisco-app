import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormDirecciones } from './form-direcciones';

describe('FormDirecciones', () => {
  let component: FormDirecciones;
  let fixture: ComponentFixture<FormDirecciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormDirecciones]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormDirecciones);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
