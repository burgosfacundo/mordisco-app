import { Component, computed, effect, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Footer } from "./shared/components/footer/footer";
import { AuthService } from './shared/services/auth-service';
import { NavbarComponent } from './shared/components/navbar/navbar';
import { NotificacionService } from './shared/services/notificacion/notificacion-service';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container-component';
import { ConfirmationDialogComponent } from './shared/components/confirmation-dialog/confirmation-dialog-component';
import { PromptDialogComponent } from "./shared/components/confirmation-dialog/confirmation-prompt-component/prompt-dialog-component";
 
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Footer, NavbarComponent, ToastContainerComponent, ConfirmationDialogComponent, PromptDialogComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnDestroy {
  protected readonly title = signal('mordisco-front');
  
  private authService = inject(AuthService);
  private notifService = inject(NotificacionService);
  private router = inject(Router);

  protected readonly isAuthenticated = this.authService.isAuthenticated;
  protected readonly currentUrl = signal(this.router.url);

  protected readonly showNavbar = computed(() => {
    const auth = this.authService.isAuthenticated();
    const url = this.currentUrl();
    return auth || !(url.startsWith('/login') || url.startsWith('/registro') 
                || url.startsWith('/recover-password') || url.startsWith('/reset-password'));
  });

  constructor() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentUrl.set(event.urlAfterRedirects);
      }
    });

    effect(() => {
      const user = this.authService.currentUser();
      const isAuth = this.authService.isAuthenticated();
      
      if (user && isAuth) {
        setTimeout(() => {
          this.notifService.conectar(user.userId, user.role);
        }, 1000);
      } else {
        this.notifService.desconectar();
      }
    });
  }

  ngOnDestroy(): void {
    this.notifService.desconectar();
  }
}