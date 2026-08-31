import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { NEVER } from 'rxjs';

import { UserService } from '../../../registro/services/user-service';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { HomeAdminComponent } from './home-admin-component';


describe('HomeAdmin', () => {
  let component: HomeAdminComponent;
  let fixture: ComponentFixture<HomeAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeAdminComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { url: [{ path: 'admin' }] } } },
        { provide: RestauranteService, useValue: { getAll: () => NEVER } },
        { provide: PedidoService, useValue: { getAll: () => NEVER } },
        { provide: UserService, useValue: { getAll: () => NEVER } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
