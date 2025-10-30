import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditProfileFormComponent } from './edit-profile-form-component';


describe('EditProfileFormComponent', () => {
  let component: EditProfileFormComponent;
  let fixture: ComponentFixture<EditProfileFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProfileFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditProfileFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
