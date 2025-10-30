import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeAdminComponent } from './home-admin-component';


describe('HomeAdmin', () => {
  let component: HomeAdminComponent;
  let fixture: ComponentFixture<HomeAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeAdminComponent]
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
