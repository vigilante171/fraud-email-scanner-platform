import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AdminUser {
  id: number;
  fullName: string;
  email: string;
  role: 'ADMIN' | 'ANALYST' | 'USER';
  active: boolean;
  createdAt: string;
}

export interface UpdateUserRequest {
  fullName: string;
  email: string;
}

export interface UpdateUserRoleRequest {
  role: 'ADMIN' | 'ANALYST' | 'USER';
}

@Injectable({
  providedIn: 'root',
})
export class AdminUserService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/api/admin/users`);
  }

  getUserById(userId: number): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.apiUrl}/api/admin/users/${userId}`);
  }

  updateUser(
    userId: number,
    payload: UpdateUserRequest,
  ): Observable<AdminUser> {
    return this.http.put<AdminUser>(
      `${this.apiUrl}/api/admin/users/${userId}`,
      payload,
    );
  }

  updateUserRole(
    userId: number,
    payload: UpdateUserRoleRequest,
  ): Observable<AdminUser> {
    return this.http.put<AdminUser>(
      `${this.apiUrl}/api/admin/users/${userId}/role`,
      payload,
    );
  }

  enableUser(userId: number): Observable<AdminUser> {
    return this.http.put<AdminUser>(
      `${this.apiUrl}/api/admin/users/${userId}/enable`,
      {},
    );
  }

  disableUser(userId: number): Observable<AdminUser> {
    return this.http.put<AdminUser>(
      `${this.apiUrl}/api/admin/users/${userId}/disable`,
      {},
    );
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/admin/users/${userId}`);
  }
}
