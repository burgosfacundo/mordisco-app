import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../services/user-service';
import UserRegister from '../../model/user-register';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification-service';

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
  private notificationService = inject(NotificationService);

  userForm! : FormGroup

  isSubmitting = signal(false);


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
      rolId: ['', Validators.required,Validators.min(1),Validators.max(4)]
    });
  }

  onSubmit(){
    if (this.userForm.invalid) return;

    this.isSubmitting.set(true);

    const user : UserRegister = this.userForm.value
    

    this.service.post(user).subscribe({
      next : () => {
        this.notificationService.success('✅ Usuario registrado correctamente')
        this.router.navigate(['/login']);
      }
    })
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.userForm.get(fieldName),
      fieldName
    );
  }
}
