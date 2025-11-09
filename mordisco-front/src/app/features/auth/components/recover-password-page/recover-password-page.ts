import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-recover-password-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './recover-password-page.html'
})
export class RecoverPasswordPage implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  protected validationService = inject(FormValidationService);

  protected recoverForm!: FormGroup;
  isSubmitting = signal(false);

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.recoverForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  enviarSolicitud(): void {
    if (this.recoverForm.invalid) {
      this.recoverForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    this.http.post<string>(
      `${environment.apiUrl}/usuarios/recover-password`,
      this.recoverForm.value
    ).subscribe({
      next: () => {
        this.snackBar.open(
          '✅ Si el email existe, recibirás un link de recuperación',
          'Cerrar',
          { duration: 5000 }
        );
        this.router.navigate(['/login']);
      },
      error: () => {
        this.snackBar.open(
          'Si el email existe, recibirás un link de recuperación',
          'Cerrar',
          { duration: 5000 }
        );
        this.isSubmitting.set(false);
      },
      complete: () => {
        this.isSubmitting.set(false);
      }
    });
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.recoverForm.get(fieldName),
      fieldName
    );
  }
}