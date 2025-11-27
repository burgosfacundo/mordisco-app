import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { MenuService } from '../../../../shared/services/menu/menu-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/services/notification-service';

@Component({
  selector: 'app-menu-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './menu-form-component.html'
})
export class MenuFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private menuService = inject(MenuService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private formValidationService = inject(FormValidationService);

  protected menuForm!: FormGroup;
  protected isSubmitting = false;
  private restauranteId?: number;

  ngOnInit(): void {
    this.initializeForm();
    this.loadRestauranteId();
  }

  private initializeForm(): void {
    this.menuForm = this.fb.group({
      nombreMenu: ['Menú Principal', [
        Validators.required, 
        Validators.minLength(3),
        Validators.maxLength(100)
      ]]
    });
  }

  private loadRestauranteId(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.restauranteId = restaurante.id;
      },
      error: () => {
        this.router.navigate(['/restaurante']);
      }
    });
  }

  onSubmit(): void {
    if (this.menuForm.invalid) {
      this.menuForm.markAllAsTouched();
      return;
    }

    if (!this.restauranteId) {
      return;
    }

    this.isSubmitting = true;
    const nombreMenu = this.menuForm.value.nombreMenu.trim();

    this.menuService.save(this.restauranteId, nombreMenu).subscribe({
      next: () => {
        this.notificationService.success('✅ Menú creado exitosamente');
        this.router.navigate(['/restaurante']);
      },
      error: () => {
        this.isSubmitting = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/restaurante']);
  }

  getError(fieldName: string): string | null {
    return this.formValidationService.getErrorMessage(
      this.menuForm.get(fieldName),
      fieldName
    );
  }
}