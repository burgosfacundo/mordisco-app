import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { PedidoCardComponent } from './pedido-card-component';


describe('PedidoCard', () => {
  let component: PedidoCardComponent;
  let fixture: ComponentFixture<PedidoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoCardComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
