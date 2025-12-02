import { Component, inject, OnInit, signal, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../../shared/services/auth-service';
import { NavbarConfig, NavbarMenuItem } from './navbar-models';
import { NavBarConfigFactory } from './navbar-config';
import { NotificacionesDropdownComponent } from "../notificaciones-dropdown/notificaciones-dropdown";
import { ConfirmDialogComponent } from '../../store/confirm-dialog-component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatTooltipModule,
    NotificacionesDropdownComponent
],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  searchTerm = '';
  showMobileMenu = signal(false);
  isHomePage = false;

  config = signal<NavbarConfig>(NavBarConfigFactory.getConfig(null, false,this.isHomePage));
  
  private searchSubject = new Subject<string>();

  currentUser = this.authService.currentUser;
  isAuthenticated = this.authService.isAuthenticated;
  
  userName = computed(() => {
    const user = this.currentUser();
    return user?.email?.split('@')[0] || 'Usuario';
  });

  constructor() {
    effect(() => {
      const user = this.currentUser();
      const authenticated = this.isAuthenticated();

      const currentUrl = this.router.url;
      this.isHomePage = currentUrl === '/home' || currentUrl === '/';
      
      this.config.set(
        NavBarConfigFactory.getConfig(user?.role ?? null, authenticated,this.isHomePage)
      );
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.emitSearchEvent(term);
    });
  }

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const url = this.router.url;
        const wasHome = this.isHomePage;
        this.isHomePage = url === '/home' || url === '/';

        if (this.isHomePage !== wasHome) {
          const user = this.currentUser();
          const authenticated = this.isAuthenticated();
          this.config.set(
            NavBarConfigFactory.getConfig(user?.role ?? null, authenticated, this.isHomePage)
          );
        }
      }
    });
  }



  onSearchInput(term: string): void {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  onSearchClear(): void {
    this.searchTerm = '';
    this.emitSearchEvent('');
  }

  private emitSearchEvent(term: string): void {
    window.dispatchEvent(new CustomEvent('search-changed', { 
      detail: { term } 
    }));
  }

  onMenuItemClick(item: NavbarMenuItem): void {
    if (item.route) {
      this.router.navigate([item.route]);
    } else if (item.action === 'logout' as any) {
      this.logout();
    }
  }

  logout(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás seguro que deseas cerrar sesión?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.authService.logout();
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToHome(): void {
    this.router.navigate(['/home']);
    this.closeMobileMenu();
  }

  getUserInitials(): string {
    const user = this.currentUser();
    if (!user?.email) return 'U';
    
    const email = user.email.split('@')[0];
    return email.substring(0, 2).toUpperCase();
  }

  toggleMobileMenu(): void {
    this.showMobileMenu.update(v => !v);
  }

  closeMobileMenu(): void {
    this.showMobileMenu.set(false);
  }




  getRoleDisplay(): string {
    const role = this.currentUser()?.role;
    if (!role) return 'USUARIO';
    return role.replace('ROLE_', '');
  }

  getBadgeClass(): string {
    const role = this.currentUser()?.role?.toLowerCase().replace('role_', '') || 'cliente';
    return `badge-${role}`;
  }
}