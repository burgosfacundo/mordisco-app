import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EleccionRegistroComponent } from './eleccion-registro.component';

describe('EleccionRegistroComponent', () => {
  let component: EleccionRegistroComponent;
  let fixture: ComponentFixture<EleccionRegistroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EleccionRegistroComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EleccionRegistroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
