import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisPedidosPage } from './mis-pedidos-page';

describe('MisPedidosPage', () => {
  let component: MisPedidosPage;
  let fixture: ComponentFixture<MisPedidosPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisPedidosPage]
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
