export interface NavbarMenuItem {
  label?: string;
  icon?: string;
  route?: string;
  action?: () => void;
  children?: NavbarMenuItem[];
  type?: 'button' | 'info'; 
  content?: string; 
}

export interface NavbarConfig {
  showSearch: boolean;
  showProfileMenu: boolean;
  showLoginButton: boolean;
  profileMenuItems: NavbarMenuItem[];
}