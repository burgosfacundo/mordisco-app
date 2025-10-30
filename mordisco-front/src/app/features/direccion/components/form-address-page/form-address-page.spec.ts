import { ComponentFixture, TestBed } from '@angular/core/testing';

import {FormAddressPage } from './form-address-page';

describe('FormAddress', () => {
  let component: FormAddressPage;
  let fixture: ComponentFixture<FormAddressPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormAddressPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormAddressPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
