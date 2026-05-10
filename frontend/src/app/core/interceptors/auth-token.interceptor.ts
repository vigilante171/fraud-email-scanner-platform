import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthTokenInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {

    // Do not attach JWT token to login/register endpoints
    if (
      request.url.includes('/api/auth/login') ||
      request.url.includes('/api/auth/register')
    ) {
      return next.handle(request);
    }

    const token = this.authService.getToken();

    if (!token) {
      return next.handle(request);
    }

    const clonedRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next.handle(clonedRequest);
  }
}