import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { NEVER } from 'rxjs';

import { UserService } from '../../../registro/services/user-service';
import { EditProfilePage } from './edit-profile-page';

describe('EditProfile', () => {
  let component: EditProfilePage;
  let fixture: ComponentFixture<EditProfilePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProfilePage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { url: [{ path: 'profile' }, { path: 'edit' }] } } },
        { provide: UserService, useValue: { getMe: () => NEVER } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditProfilePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
