import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
})
export class LayoutComponent {
  constructor(private authService: AuthService) {}

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  getUserEmail(): string | null {
    return localStorage.getItem('email');
  }

  getFullName(): string | null {
    return localStorage.getItem('fullName');
  }

  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  getProfilePictureUrl(): string | null {
    const url = localStorage.getItem('profilePictureUrl');

    if (!url) {
      return null;
    }

    if (url.startsWith('http')) {
      return url;
    }

    return `http://localhost:8081${url}`;
  }

  logout(): void {
    this.authService.logout();
  }
}
