import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormAddress } from './form-address';

describe('FormAddress', () => {
  let component: FormAddress;
  let fixture: ComponentFixture<FormAddress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormAddress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormAddress);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
