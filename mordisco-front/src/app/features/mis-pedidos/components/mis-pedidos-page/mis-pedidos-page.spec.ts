import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { MisPedidosPage } from './mis-pedidos-page';

describe('MisPedidosPage', () => {
  let component: MisPedidosPage;
  let fixture: ComponentFixture<MisPedidosPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisPedidosPage],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MisPedidosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
