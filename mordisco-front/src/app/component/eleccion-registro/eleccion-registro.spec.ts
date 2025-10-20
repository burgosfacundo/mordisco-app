import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EleccionRegistro } from './eleccion-registro';

describe('EleccionRegistro', () => {
  let component: EleccionRegistro;
  let fixture: ComponentFixture<EleccionRegistro>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EleccionRegistro]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EleccionRegistro);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
