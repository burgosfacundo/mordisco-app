import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../../auth/services/auth-service';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css'
})
export class LoginForm implements OnInit {
  // Inyecciones
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  // Form controls
  protected email!: FormControl<string | null>;
  protected password!: FormControl<string | null>;
  protected loginForm!: FormGroup;

  // Estado del componente
  isSubmitting = signal(false);

  constructor() {
    this.initializeForm();
  }

  ngOnInit(): void {
    this.buildForm();
  }

  private initializeForm(): void {
    this.email = new FormControl('', [Validators.required, Validators.email]);
    this.password = new FormControl('', [
      Validators.required,
      Validators.minLength(6)
    ]);
  }

  private buildForm(): void {
    this.loginForm = this.fb.group({
      email: this.email,
      password: this.password
    });
  }

  iniciarSesion(): void {
    if (this.loginForm.invalid) return;

    this.isSubmitting.set(true);

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('✅ Inicio de sesión exitoso', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error de autenticación:', error);
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

  // Helpers para validación en template
  get emailErrors() {
    const control = this.loginForm.get('email');
    if (!control?.touched) return null;
    
    if (control.hasError('required')) return 'El email es obligatorio';
    if (control.hasError('email')) return 'El formato del email no es válido';
    return null;
  }

  get passwordErrors() {
    const control = this.loginForm.get('password');
    if (!control?.touched) return null;
    
    if (control.hasError('required')) return 'La contraseña es obligatoria';
    if (control.hasError('minLength')) return 'La contraseña debe tener al menos 6 caracteres';
    return null;
  }
}