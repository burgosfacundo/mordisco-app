export interface NavbarMenuItem {
  label: string;
  icon: string;
  route?: string;
  action?: () => void;
  children?: NavbarMenuItem[];
}

export interface NavbarConfig {
  showSearch: boolean;
  showCitySelector: boolean;
  showProfileMenu: boolean;
  showLoginButton: boolean;
  profileMenuItems: NavbarMenuItem[];
}