import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EleccionUsuario } from './eleccion-usuario';

describe('EleccionUsuario', () => {
  let component: EleccionUsuario;
  let fixture: ComponentFixture<EleccionUsuario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EleccionUsuario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EleccionUsuario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
