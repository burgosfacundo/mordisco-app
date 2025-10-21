import { inject } from '@angular/core';
import { CanMatchFn, GuardResult, MaybeAsync, Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth-service';
import { map } from 'rxjs';

export const authGuard: CanMatchFn = (route, segments) : MaybeAsync<GuardResult> => {
  return inject(AuthService).currentUser$.pipe(
    map(u => u ? true : false)
  ) 
};
