import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../services/user-service';
import UserRegister from '../../model/user-register';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastService } from '../../../../core/services/toast-service';
import {
  NOMBRE_PATTERN, NOMBRE_MIN_LENGTH, NOMBRE_MAX_LENGTH,
  TELEFONO_PATTERN, TELEFONO_MIN_LENGTH, TELEFONO_MAX_LENGTH,
  PASSWORD_PATTERN, PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH,
  EMAIL_MIN_LENGTH, EMAIL_MAX_LENGTH
} from '../../../../shared/validators/validation-constants';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './user-form-component.html'
})
export class UserFormComponent implements OnInit{
  private router = inject(Router)
  private service : UserService = inject(UserService)
  private validationService = inject(FormValidationService)
  private fb : FormBuilder = inject(FormBuilder)
  private toastService = inject(ToastService);

  userForm! : FormGroup

  isSubmitting = signal(false);
  showPassword = false;
  showPassword2 = false;

  ngOnInit(): void {
    this.inicializarFormulario()
  }

  inicializarFormulario(){
     this.userForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(NOMBRE_MIN_LENGTH), Validators.maxLength(NOMBRE_MAX_LENGTH), Validators.pattern(NOMBRE_PATTERN)]],
      apellido: ['', [Validators.required, Validators.minLength(NOMBRE_MIN_LENGTH), Validators.maxLength(NOMBRE_MAX_LENGTH), Validators.pattern(NOMBRE_PATTERN)]],
      telefono: ['', [Validators.required, Validators.minLength(TELEFONO_MIN_LENGTH), Validators.maxLength(TELEFONO_MAX_LENGTH), Validators.pattern(TELEFONO_PATTERN)]],
      email: ['', [Validators.required, Validators.minLength(EMAIL_MIN_LENGTH), Validators.email, Validators.maxLength(EMAIL_MAX_LENGTH)]],
      password: ['', [Validators.required, Validators.minLength(PASSWORD_MIN_LENGTH), Validators.maxLength(PASSWORD_MAX_LENGTH), Validators.pattern(PASSWORD_PATTERN)]],
      password2: ['', [Validators.required, Validators.minLength(PASSWORD_MIN_LENGTH), Validators.maxLength(PASSWORD_MAX_LENGTH), Validators.pattern(PASSWORD_PATTERN)]],
      rolId: ['', [Validators.required, Validators.min(1), Validators.max(4)]]
    }, { validators : this.passwordsMatchValidator});
  }

  onSubmit(){
    if (this.userForm.invalid) return;

    this.isSubmitting.set(true);

    const user = { ...this.userForm.value };
    delete user.password2;
    
    this.service.post(user).subscribe({
      next : () => {
        this.toastService.success('Usuario registrado correctamente')
        this.router.navigate(['/login']);
      },
      error : () => {
        this.isSubmitting.set(false);
      },
    })
  }

  passwordsMatchValidator(control: AbstractControl): { passwordMismatch: boolean } | null {
    const password = control.get('password')?.value;
    const password2 = control.get('password2')?.value;
    
    return password === password2 ? null : { passwordMismatch: true };
  }

  getError(fieldName: string): string | null {
    const control = this.userForm.get(fieldName);
    
    // Error específico para password2
    if (fieldName === 'password2' && this.userForm.hasError('passwordMismatch') && control?.touched) {
      return '*Las contraseñas no coinciden';
    }
    
    return this.validationService.getErrorMessage(control, fieldName);
  }
  
  // Métodos para toggle
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  togglePassword2Visibility() {
    this.showPassword2 = !this.showPassword2;
  }  
 
}
