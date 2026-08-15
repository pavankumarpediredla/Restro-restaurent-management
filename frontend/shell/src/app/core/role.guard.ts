import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _state: RouterStateSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const allowedRoles = (route.data['roles'] as Array<string> | undefined) ?? [];
  const user = auth.currentUser();

  if (!user) {
    router.navigate(['/login']);
    return false;
  }

  if (user.role === 'OWNER' || allowedRoles.length === 0 || allowedRoles.includes(user.role)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
