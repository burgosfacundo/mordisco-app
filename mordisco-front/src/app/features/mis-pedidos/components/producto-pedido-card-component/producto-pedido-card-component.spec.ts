import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductoPedidoCardComponent } from './producto-pedido-card-component';

describe('ProductoPedidoCardComponent', () => {
  let component: ProductoPedidoCardComponent;
  let fixture: ComponentFixture<ProductoPedidoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoPedidoCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductoPedidoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
