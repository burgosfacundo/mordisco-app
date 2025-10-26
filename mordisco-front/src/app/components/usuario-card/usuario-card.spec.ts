import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsuarioCard } from './usuario-card';

describe('UsuarioCard', () => {
  let component: UsuarioCard;
  let fixture: ComponentFixture<UsuarioCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsuarioCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsuarioCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
