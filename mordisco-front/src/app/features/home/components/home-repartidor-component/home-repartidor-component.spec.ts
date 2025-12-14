import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeRepartidorComponent } from './home-repartidor-component';

describe('HomeRepartidorComponent', () => {
  let component: HomeRepartidorComponent;
  let fixture: ComponentFixture<HomeRepartidorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeRepartidorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeRepartidorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
