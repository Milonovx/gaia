import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';
import { StorageService } from '../services/storage.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(StorageService);
  const token = storage.getToken();
  const isApiRequest = req.url.startsWith(environment.apiUrl);
  const isAuthRequest = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  if (!token || !isApiRequest || isAuthRequest) {
    return next(req);
  }

  const auth = inject(AuthService);
  if (auth.isTokenExpired()) {
    auth.logout(false);
    return next(req);
  }

  return next(req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  }));
};
