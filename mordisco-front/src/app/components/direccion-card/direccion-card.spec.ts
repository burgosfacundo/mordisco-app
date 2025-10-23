import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DireccionCard } from './direccion-card';

describe('DireccionCard', () => {
  let component: DireccionCard;
  let fixture: ComponentFixture<DireccionCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DireccionCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DireccionCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
