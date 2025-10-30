import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeClienteComponent } from './home-cliente-component';


describe('HomeClienteComponent', () => {
  let component: HomeClienteComponent;
  let fixture: ComponentFixture<HomeClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeClienteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
