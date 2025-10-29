import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Footer } from "./shared/components/footer/footer";
import { AuthService } from './shared/services/auth-service';
import { NavbarComponent } from './shared/components/navbar/navbar';
 
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
