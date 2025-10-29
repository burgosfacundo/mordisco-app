import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { AuthService } from '../../../../shared/services/auth-service';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-form.html'
})
export class LoginForm implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  protected validationService = inject(FormValidationService);

  protected loginForm!: FormGroup;

  isSubmitting = signal(false);

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  iniciarSesion(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('✅ Inicio de sesión exitoso', 'Cerrar', { duration: 3000 });
        
   
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 100);
      },
      error: () => {
        this.snackBar.open('❌ Email o contraseña incorrectos', 'Cerrar', { 
          duration: 4000 
        });
        this.isSubmitting.set(false);
      },
      complete: () => {
        this.isSubmitting.set(false);
      }
    });
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.loginForm.get(fieldName),
      fieldName
    );
  }
}