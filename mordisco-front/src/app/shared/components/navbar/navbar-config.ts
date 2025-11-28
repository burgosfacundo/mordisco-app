import { NavbarConfig } from "./navbar-models";

export class NavBarConfigFactory {
  
  static getConfig(role: string | null, isAuthenticated: boolean, home : boolean): NavbarConfig {
    if (!isAuthenticated) {
      return this.getPublicConfig();
    }

    switch (role) {
      case 'ROLE_ADMIN':
        return this.getAdminConfig();
      case 'ROLE_RESTAURANTE':
        return this.getRestauranteConfig();
      case 'ROLE_CLIENTE':
        return this.getClienteConfig(home);
      case 'ROLE_REPARTIDOR':
        return this.getRepartidorConfig(home);
      default:
        return this.getPublicConfig();
    }
  }

  private static getPublicConfig(): NavbarConfig {
    return {
      showSearch: true,
      showCitySelector: true,
      showProfileMenu: false,
      showLoginButton: true,
      profileMenuItems: []
    };
  }

  private static getClienteConfig(home : boolean): NavbarConfig {
    if(home){
      return {
        showSearch: true,
        showCitySelector: true,
        showProfileMenu: true,
        showLoginButton: false,
        profileMenuItems: [
          { label: 'Mi Perfil', icon: 'person', route: '/profile' },
          { label: 'Mis Direcciones', icon: 'home',route: '/cliente/my-address'},
          { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
          { label: 'Mis Pedidos', icon: 'receipt_long', route: '/cliente/pedidos' },
          { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
        ]
      }
    } else {
      return {
        showSearch: false,
        showCitySelector: false,
        showProfileMenu: true,
        showLoginButton: false,
        profileMenuItems: [
          { label: 'Mi Perfil', icon: 'person', route: '/profile' },
          { label: 'Mis Direcciones', icon: 'home',route: '/cliente/my-address'},
          { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
          { label: 'Mis Pedidos', icon: 'receipt_long', route: '/cliente/pedidos' },
          { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
        ]
      }
    }
  }

  private static getRestauranteConfig(): NavbarConfig {
    return {
      showSearch: false,
      showCitySelector: false,
      showProfileMenu: true,
      showLoginButton: false,
      profileMenuItems: [
        { label: 'Mi Perfil', icon: 'person', route: '/profile' },
        { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
        { label: 'Mi Restaurante', icon: 'restaurant', route: '/restaurante' },
        { label: 'Menú', icon: 'menu_book', route: '/restaurante/menu' },
        { label: 'Pedidos', icon: 'receipt_long', route: '/restaurante/pedidos' },
        { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
      ]
    }
  }

  private static getAdminConfig(): NavbarConfig {
    return {
      showSearch: false,
      showCitySelector: false,
      showProfileMenu: true,
      showLoginButton: false,
      profileMenuItems: [
        { label: 'Mi Perfil', icon: 'person', route: '/profile' },
        { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
        { label: 'Ver Restaurantes', icon: 'restaurant', route: '/admin/restaurantes' },
        { label: 'Ver Pedidos', icon: 'receipt_long', route: '/admin/pedidos' },
        { label: 'Ver Usuarios', icon: 'people', route: '/admin/usuarios' },
        { label: 'Estadísticas', icon: 'analytics', route: '/admin/estadisticas' },
        { label: 'Configuracion del sistema', icon: 'settings', route: '/admin/configuracion' },        
        { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
      ]
    };
  }

  private static getRepartidorConfig(home : boolean): NavbarConfig {
    if(home){
      return {
        showSearch: false,
        showCitySelector: false,
        showProfileMenu: true,
        showLoginButton: false,
        profileMenuItems: [
          { label: 'Mi Perfil', icon: 'person', route: '/profile' },
          { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
          { label: 'Mis Calificaciones', icon: 'delivery_dining', route: '/repartidor/calificaciones' },
          { label: 'Historial', icon: 'history', route: '/repartidor/pedidos/historial' },
          { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
        ]
      }
    } else {
      return {
        showSearch: false,
        showCitySelector: false,
        showProfileMenu: true,
        showLoginButton: false,
        profileMenuItems: [
          { label: 'Mi Perfil', icon: 'person', route: '/profile' },
          { label: 'Cambiar Contraseña', icon: 'lock', route: '/edit-password' },
          { label: 'Mis Entregas', icon: 'delivery_dining', route: '/repartidor/entregas' },
          { label: 'Historial', icon: 'history', route: '/repartidor/historial' },
          { label: 'Cerrar Sesión', icon: 'logout', action: 'logout' as any }
        ]
      };
    }
  }

}