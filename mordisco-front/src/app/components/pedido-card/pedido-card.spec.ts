import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoCard } from './pedido-card';

describe('PedidoCard', () => {
  let component: PedidoCard;
  let fixture: ComponentFixture<PedidoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidoCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
