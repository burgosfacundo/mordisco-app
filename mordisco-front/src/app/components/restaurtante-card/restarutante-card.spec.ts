import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestarutanteCard } from './restarutante-card';

describe('RestarutanteCard', () => {
  let component: RestarutanteCard;
  let fixture: ComponentFixture<RestarutanteCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestarutanteCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestarutanteCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
