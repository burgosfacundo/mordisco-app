import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../../../shared/services/auth-service';
import { HomeRestauranteComponent } from '../home-restaurante-component/home-restaurante-component';
import { HomeClienteComponent } from '../home-cliente-component/home-cliente-component';
import { HomeAdminComponent } from '../home-admin-component/home-admin-component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, HomeRestauranteComponent, HomeClienteComponent, HomeAdminComponent],
  templateUrl: './redirect-home-page.html',
  styleUrl: './redirect-home-page.css'
})
export class HomePage implements OnInit {
  protected authService = inject(AuthService);
  
  userRole: string | null = null;
  isLoading = true;

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    
    if (user) {
      this.userRole = user.role;
      this.isLoading = false;
    } else {
      this.authService.refreshToken().subscribe({
        next: () => {
          const refreshedUser = this.authService.currentUser();
          this.userRole = refreshedUser?.role ?? null;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.authService.logout();
        }
      });
    }
  }
}