import { Component, inject, OnInit, signal, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { NavBarConfigFactory } from './navbar-config';
import { CiudadService } from '../../services/ciudad/ciudad-service';
import { Ciudad } from '../../models/ciudad/ciudad';
import { AuthService } from '../../services/auth-service';
import { NavbarConfig, NavbarMenuItem } from './navbar-models';

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
    MatTooltipModule
],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private ciudadService = inject(CiudadService);
  private router = inject(Router);
  isHomePage = false;

  config = signal<NavbarConfig>(NavBarConfigFactory.getConfig(null, false,this.isHomePage));
  ciudades = signal<Ciudad[]>([]);
  searchTerm = '';

  private searchSubject = new Subject<string>();
  
  currentUser = this.authService.currentUser;
  isAuthenticated = this.authService.isAuthenticated;
  ciudadSeleccionada = this.ciudadService.ciudadSeleccionada;

  userName = computed(() => {
    const user = this.currentUser();
    return user?.email?.split('@')[0] || 'Usuario';
  });

  constructor() {
    const currentUrl = this.router.url;
    this.isHomePage = currentUrl === '/home' || currentUrl === '/';

    effect(() => {
      const user = this.currentUser();
      const authenticated = this.isAuthenticated();
      
      this.config.set(
        NavBarConfigFactory.getConfig(user?.role ?? null, authenticated, this.isHomePage)
      );
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.emitSearchEvent(term);
    });

    if (this.isHomePage) {
      this.loadCiudades();
    }

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

        if (this.isHomePage && this.ciudades().length === 0) {
          this.loadCiudades();
        }
      }
    });
     
  }


  compareCiudades = (c1: Ciudad | null, c2: Ciudad | null): boolean => {
    if (c1 == null && c2 == null) return true;
    if (c1 == null || c2 == null) return false;
    return c1.id === c2.id;
  };


  private loadCiudades(): void {
    this.ciudadService.getCiudades().subscribe({
      next: (ciudades) => {
        this.ciudades.set(ciudades);
        
        if (!this.ciudadSeleccionada() && ciudades.length > 0) {
          this.ciudadService.setCiudad(ciudades[0]);
        }
      },
      error: (err) => {
        console.error('Error cargando ciudades:', err)
        
        this.ciudades.set([this.ciudadSeleccionada()]);
      }
    });
  }

 onCiudadChange(ciudad: Ciudad): void {
    this.ciudadService.setCiudad(ciudad);
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
      detail: { 
        term,
        ciudad: this.ciudadSeleccionada().nombre 
      } 
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
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
      this.authService.logout();
      this.router.navigate(['/login'])
    }
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  getUserInitials(): string {
    const user = this.currentUser();
    if (!user?.email) return 'U';
    
    const email = user.email.split('@')[0];
    return email.substring(0, 2).toUpperCase();
  }

}