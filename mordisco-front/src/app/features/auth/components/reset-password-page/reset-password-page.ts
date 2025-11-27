import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { environment } from '../../../../../environments/environment';
import { NotificationService } from '../../../../core/services/notification-service';

@Component({
    selector: 'app-reset-password-page',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './reset-password-page.html'
})
export class ResetPasswordPage implements OnInit {
    private fb = inject(FormBuilder);
    private http = inject(HttpClient);
    private router = inject(Router);
    private route = inject(ActivatedRoute);
    private notificationService = inject(NotificationService);
    protected validationService = inject(FormValidationService);
    protected resetForm!: FormGroup;
    isSubmitting = signal(false);
    private token: string = '';

    ngOnInit(): void {
        this.token = this.route.snapshot.queryParamMap.get('token') || '';
        if (!this.token) {
            this.router.navigate(['/login']);
            return;
        }

        this.initializeForm();
    }

    private initializeForm(): void {
        this.resetForm = this.fb.group({
            newPassword: ['', [
                Validators.required,
                Validators.minLength(8),
                Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@!!%*?&])[A-Za-z\d@!!%*?&]+$/)
                ]
            ],
            confirmPassword: ['', [Validators.required]]
        }, 
        {
            validators: this.passwordMatchValidator
        });
    }

    private passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
        const newPassword = control.get('newPassword');
        const confirmPassword = control.get('confirmPassword');
        if (!newPassword || !confirmPassword) {
            return null;
        }

        return newPassword.value === confirmPassword.value ? null : { passwordMismatch: true };
    }

    resetearPassword(): void {
        if (this.resetForm.invalid) {
            this.resetForm.markAllAsTouched();
            return;
        }
        this.isSubmitting.set(true);

        const payload = {
            token: this.token,
            newPassword: this.resetForm.value.newPassword
        };

        this.http.post<string>(`${environment.apiUrl}/usuarios/reset-password`,payload).subscribe({
            next: () => {
                this.notificationService.success('✅ Contraseña restablecida correctamente');
            },
            complete: () => {
                this.isSubmitting.set(false);
                this.router.navigate(['/login']);
            }
        });
    }

    getError(fieldName: string): string | null {
        const control = this.resetForm.get(fieldName);
        if (fieldName === 'confirmPassword' && this.resetForm.errors?.['passwordMismatch'] && control?.touched) {
            return 'Las contraseñas no coinciden';
        }

        return this.validationService.getErrorMessage(control, fieldName);
    }
}