import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyAddress } from './my-address';

describe('MyAddress', () => {
  let component: MyAddress;
  let fixture: ComponentFixture<MyAddress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyAddress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyAddress);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
