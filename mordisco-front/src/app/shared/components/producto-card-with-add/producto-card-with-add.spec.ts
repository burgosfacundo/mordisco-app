import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductoCardWithAdd } from './producto-card-with-add';

describe('ProductoCardWithAdd', () => {
  let component: ProductoCardWithAdd;
  let fixture: ComponentFixture<ProductoCardWithAdd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoCardWithAdd]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductoCardWithAdd);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
