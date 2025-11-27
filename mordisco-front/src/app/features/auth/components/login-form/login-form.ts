import { Component, inject, OnInit, signal } from '@angular/core';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification-service';

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
  private notificationService = inject(NotificationService);
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
        this.notificationService.success('✅ Inicio de sesión exitoso');
        this.isSubmitting.set(false);
        
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 100);
      },
      error: () => {
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