import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProfileForm } from './edit-profile-form';

describe('EditProfileForm', () => {
  let component: EditProfileForm;
  let fixture: ComponentFixture<EditProfileForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProfileForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditProfileForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
