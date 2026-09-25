import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { ToastService } from '../../../../core/services/toast-service';
import { EditPasswordComponent } from './edit-password-component';

describe('EditPasswordComponent', () => {
  let component: EditPasswordComponent;
  let fixture: ComponentFixture<EditPasswordComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;
  let toastService: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', [
      'updatePassword',
      'clearAuthSilently'
    ]);
    authService.updatePassword.and.returnValue(of(void 0));
    toastService = jasmine.createSpyObj<ToastService>('ToastService', ['success', 'error']);

    await TestBed.configureTestingModule({
      imports: [EditPasswordComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: ToastService, useValue: toastService },
        { provide: FormValidationService, useValue: { getErrorMessage: () => null } }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture = TestBed.createComponent(EditPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const setPasswordValues = (newPassword = 'NewPassword1!'): void => {
    component.editarPassword.setValue({
      passwordActual: 'OldPassword1!',
      password: newPassword,
      confirmarPasswordNueva: newPassword
    });
  };

  it('clears the authenticated session and navigates to login after a successful password change', () => {
    setPasswordValues();

    component.manejarModificacionPassword();

    expect(authService.updatePassword).toHaveBeenCalledWith({
      currentPassword: 'OldPassword1!',
      newPassword: 'NewPassword1!'
    });
    expect(authService.clearAuthSilently).toHaveBeenCalledTimes(1);
    expect(toastService.success).toHaveBeenCalledWith('✅ Contraseña actualizada correctamente');
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('does not update or clear the session when the new passwords do not match', () => {
    component.editarPassword.setValue({
      passwordActual: 'OldPassword1!',
      password: 'NewPassword1!',
      confirmarPasswordNueva: 'DifferentPassword2!'
    });

    component.manejarModificacionPassword();

    expect(authService.updatePassword).not.toHaveBeenCalled();
    expect(authService.clearAuthSilently).not.toHaveBeenCalled();
    expect(toastService.error).toHaveBeenCalledWith('❌ Las contraseñas nuevas no coinciden');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('keeps the session when password update fails', () => {
    authService.updatePassword.and.returnValue(throwError(() => new Error('Password update failed')));
    setPasswordValues();

    component.manejarModificacionPassword();

    expect(authService.clearAuthSilently).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.isSubmitting()).toBeFalse();
  });
});
