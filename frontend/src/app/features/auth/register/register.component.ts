import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  form = {
    fullName: '',
    email: '',
    password: '',
    role: 'USER'
  };

  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService) {}

  register(registerForm: NgForm): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (registerForm.invalid) {
      this.errorMessage = 'Please enter a valid email and a password with at least 6 characters.';
      return;
    }

    this.authService.register(this.form).subscribe({
      next: () => {
        this.successMessage = 'Account created successfully. You can now login.';
        this.form = {
          fullName: '',
          email: '',
          password: '',
          role: 'USER'
        };
        registerForm.resetForm();
      },
      error: (error) => {
        console.error('FULL REGISTER ERROR:', error);

        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.status === 409) {
          this.errorMessage = 'This email is already used.';
        } else {
          this.errorMessage = `Registration failed. Status: ${error.status}`;
        }
      }
    });
  }
}