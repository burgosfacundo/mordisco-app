import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Footer } from "./components/footer/footer";
import { NavbarComponent } from "./components/navbar/navbar";
import { AuthService } from './auth/services/auth-service';
 
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Footer, NavbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('mordisco-front');
  private authService = inject(AuthService);
  private router = inject(Router);

  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected readonly currentUrl = signal(this.router.url);

  protected readonly showNavbar = computed(() => {
    const auth = this.authService.isAuthenticated();
    const url = this.currentUrl();
    return auth || !(url.startsWith('/login') || url.startsWith('/registro'));
  });

  constructor() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentUrl.set(event.urlAfterRedirects);
      }
    });
  }
}
