import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { NEVER, of } from 'rxjs';

import { ToastService } from '../../../../core/services/toast-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { ConfiguracionSistemaService } from '../../../../shared/services/configuracionSistema/configuracion-sistema-service';
import { ProductoService } from '../../../../shared/services/productos/producto-service';
import { ProductoFormComponent } from './producto-form-component';

describe('ProductoFormComponent', () => {
  let component: ProductoFormComponent;
  let fixture: ComponentFixture<ProductoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoFormComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ menuId: '1' }),
            snapshot: { url: [{ path: 'restaurante' }, { path: 'producto' }, { path: 'nuevo' }] }
          }
        },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } },
        { provide: ProductoService, useValue: { clearProductoToEdit: jasmine.createSpy('clearProductoToEdit') } },
        { provide: ConfiguracionSistemaService, useValue: { getConfiguracionGeneral: () => NEVER } },
        { provide: ToastService, useValue: { warning: jasmine.createSpy('warning') } },
        { provide: MatDialog, useValue: {} },
        FormValidationService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
