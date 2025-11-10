import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyAddressPage } from './my-address-page';

describe('MyAddress', () => {
  let component: MyAddressPage;
  let fixture: ComponentFixture<MyAddressPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyAddressPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyAddressPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
