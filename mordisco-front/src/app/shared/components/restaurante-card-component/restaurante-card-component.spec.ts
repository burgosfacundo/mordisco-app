import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RestauranteCardComponent } from './restaurante-card-component';


describe('RestauranteCardComponent', () => {
  let component: RestauranteCardComponent;
  let fixture: ComponentFixture<RestauranteCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestauranteCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestauranteCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
