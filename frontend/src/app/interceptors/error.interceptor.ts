import { HttpErrorResponse, HttpEventType, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { TimeoutError, catchError, tap, throwError, timeout } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';

let handlingUnauthorized = false;

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const toastr = inject(ToastrService);
  const auth = inject(AuthService);
  const isApiRequest = req.url.startsWith(environment.apiUrl);
  const startedAt = Date.now();

  if (isApiRequest && !environment.production) {
    console.debug('[GAIA HTTP] request', req.method, req.urlWithParams);
  }

  return next(req).pipe(
    timeout(environment.httpTimeoutMs),
    tap((event) => {
      if (isApiRequest && !environment.production && event.type === HttpEventType.Response) {
        console.debug('[GAIA HTTP] response', req.method, req.urlWithParams, event.status, `${Date.now() - startedAt}ms`);
      }
    }),
    catchError((error: HttpErrorResponse | TimeoutError) => {
      const isTimeout = error instanceof TimeoutError;
      const status = isTimeout ? 0 : error.status;
      const backendMessage = !isTimeout && typeof error.error?.message === 'string' ? error.error.message : null;
      const message = isTimeout
        ? 'La solicitud tardo demasiado y fue cancelada.'
        : backendMessage || 'No fue posible completar la operacion';
      const isAuthRequest = req.url.includes('/auth/login') || req.url.includes('/auth/register');

      if (isApiRequest && !environment.production) {
        console.error('[GAIA HTTP] error', req.method, req.urlWithParams, status, message, error);
      }

      if (status === 401 && !isAuthRequest) {
        auth.logout(false);
        const currentPath = router.url.split('?')[0];
        if (!handlingUnauthorized && currentPath !== '/login') {
          handlingUnauthorized = true;
          toastr.warning('Tu sesion expiro. Inicia sesion nuevamente.');
          router.navigate(['/login'], { queryParams: { returnUrl: router.url } })
            .finally(() => handlingUnauthorized = false);
        }
      } else if (status === 403) {
        toastr.error('No tienes permisos para realizar esta accion.');
      } else if (status >= 500) {
        toastr.error('Error interno del servidor.');
      } else if (message) {
        toastr.error(message);
      }

      return throwError(() => error);
    })
  );
};
