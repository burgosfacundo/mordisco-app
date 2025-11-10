import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../shared/services/auth-service';
import { HomeRestauranteComponent } from '../home-restaurante-component/home-restaurante-component';
import { HomeClienteComponent } from '../home-cliente-component/home-cliente-component';
import { HomeAdminComponent } from '../home-admin-component/home-admin-component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    CommonModule,
    HomeRestauranteComponent,
    HomeClienteComponent,
    HomeAdminComponent
  ],
  templateUrl: './redirect-home-page.html'
})
export class HomePage implements OnInit {
  protected authService = inject(AuthService);
  
  userRole: string | null = null;
  isAuthenticated = false;
  isLoading = true;

  ngOnInit(): void {
    const hasToken = this.authService.getAccessToken();
    
    if (!hasToken) {
      this.isAuthenticated = false;
      this.userRole = null;
      this.isLoading = false;
      return;
    }

    const user = this.authService.currentUser();
    
    if (user) {
      this.isAuthenticated = true;
      this.userRole = user.role;
      this.isLoading = false;
    } else {
      this.authService.refreshToken().subscribe({
        next: () => {
          const refreshedUser = this.authService.currentUser();
          this.isAuthenticated = true;
          this.userRole = refreshedUser?.role ?? null;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error al refrescar token:', err);
          this.isAuthenticated = false;
          this.userRole = null;
          this.isLoading = false;
          
          this.authService.clearAuthSilently();
        }
      });
    }
  }
}