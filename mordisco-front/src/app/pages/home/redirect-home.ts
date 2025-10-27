import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/services/auth-service';
import { HomeRestaurante } from "../../components/home/home-restaurante/home-restaurante";
import { HomeCliente } from "../../components/home/home-cliente/home-cliente";
import { HomeAdmin } from "../../components/home/home-admin/home-admin";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HomeRestaurante, HomeCliente, HomeAdmin],
  templateUrl: './redirect-home.html',
  styleUrl: './redirect-home.css'
})
export class Home implements OnInit {
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