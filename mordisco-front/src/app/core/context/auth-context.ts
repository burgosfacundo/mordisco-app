import { HttpContextToken } from '@angular/common/http';

// Usarlo para marcar requests que NO deben llevar Authorization JWT
export const SKIP_AUTH = new HttpContextToken<boolean>(() => false);
