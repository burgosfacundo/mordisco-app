import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../services/user-service';
import UserRegister from '../../model/user-register';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastService } from '../../../../core/services/toast-service';

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
      nombre: ['', [Validators.required, Validators.minLength(2),Validators.maxLength(50),Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/)]],
      apellido: ['', [Validators.required, Validators.minLength(2),Validators.maxLength(50),Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/)]],
      telefono: ['', [Validators.required,Validators.minLength(10),Validators.maxLength(50),Validators.pattern(/^\+\d{1,3}(?:\s?\d){6,14}$/)]],
      email: ['', [Validators.required, Validators.minLength(5),Validators.email,Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8),Validators.maxLength(50), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]],
      password2: ['', [Validators.required, Validators.minLength(8),Validators.maxLength(50), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]],      
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
