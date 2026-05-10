import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

interface AuthResponse {
  token: string;
  email?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(data: { email: string; password: string }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/api/auth/login`, data)
      .pipe(
        tap((response) => {
          localStorage.setItem('token', response.token);
          if (response.email) {
            localStorage.setItem('email', response.email);
          }
          if (response.role) {
            localStorage.setItem('role', response.role);
          }
        }),
      );
  }

  register(data: {
    fullName: string;
    email: string;
    password: string;
  }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/api/auth/register`,
      data,
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
