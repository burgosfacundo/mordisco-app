import { ComponentFixture, TestBed } from '@angular/core/testing';

import ProductoResponse from '../../models/producto/producto-response';
import { ProductoCardWithAdd } from './producto-card-with-add';

describe('ProductoCardWithAdd', () => {
  let component: ProductoCardWithAdd;
  let fixture: ComponentFixture<ProductoCardWithAdd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoCardWithAdd]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductoCardWithAdd);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('producto', {
      id: 1,
      idMenu: 1,
      nombre: 'Test product',
      descripcion: 'Test description',
      precio: 1000,
      tienePromocion: false,
      disponible: true,
      imagen: { id: 1, url: 'https://example.com/product.jpg', nombre: 'Test product' }
    } satisfies ProductoResponse);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
