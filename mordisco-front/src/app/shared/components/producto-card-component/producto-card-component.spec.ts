import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductoCardComponent } from './producto-card-component';

describe('ProductoCardComponent', () => {
  let component: ProductoCardComponent;
  let fixture: ComponentFixture<ProductoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
