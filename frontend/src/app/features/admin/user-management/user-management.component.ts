import { Component, OnInit } from '@angular/core';
import {
  AdminUser,
  AdminUserService,
} from '../../../core/services/admin-user.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
})
export class UserManagementComponent implements OnInit {
  users: AdminUser[] = [];
  filteredUsers: AdminUser[] = [];

  loading = false;
  saving = false;

  errorMessage = '';
  successMessage = '';

  searchTerm = '';
  selectedRole = 'ALL';
  selectedStatus = 'ALL';

  editMode = false;
  selectedUserId: number | null = null;

  editForm = {
    fullName: '',
    email: '',
    role: 'USER' as 'ADMIN' | 'ANALYST' | 'USER',
  };

  roles: Array<'ADMIN' | 'ANALYST' | 'USER'> = ['ADMIN', 'ANALYST', 'USER'];

  constructor(private adminUserService: AdminUserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminUserService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users || [];
        this.filteredUsers = this.users;
        this.loading = false;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Load users error:', error);
        this.errorMessage =
          'Failed to load users. Make sure auth-service is running.';
        this.loading = false;
      },
    });
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredUsers = this.users.filter((user) => {
      const matchesSearch =
        !term ||
        user.fullName?.toLowerCase().includes(term) ||
        user.email?.toLowerCase().includes(term) ||
        user.role?.toLowerCase().includes(term) ||
        String(user.id).includes(term);

      const matchesRole =
        this.selectedRole === 'ALL' || user.role === this.selectedRole;

      const matchesStatus =
        this.selectedStatus === 'ALL' ||
        (this.selectedStatus === 'ACTIVE' && user.active) ||
        (this.selectedStatus === 'DISABLED' && !user.active);

      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedRole = 'ALL';
    this.selectedStatus = 'ALL';
    this.filteredUsers = this.users;
  }

  openEdit(user: AdminUser): void {
    this.editMode = true;
    this.selectedUserId = user.id;
    this.successMessage = '';
    this.errorMessage = '';

    this.editForm = {
      fullName: user.fullName,
      email: user.email,
      role: user.role,
    };
  }

  cancelEdit(): void {
    this.editMode = false;
    this.selectedUserId = null;

    this.editForm = {
      fullName: '',
      email: '',
      role: 'USER',
    };
  }

  saveUser(): void {
    if (!this.selectedUserId) {
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminUserService
      .updateUser(this.selectedUserId, {
        fullName: this.editForm.fullName,
        email: this.editForm.email,
      })
      .subscribe({
        next: () => {
          this.adminUserService
            .updateUserRole(this.selectedUserId as number, {
              role: this.editForm.role,
            })
            .subscribe({
              next: () => {
                this.saving = false;
                this.successMessage = 'User updated successfully.';
                this.cancelEdit();
                this.loadUsers();
              },
              error: (error) => {
                console.error('Update role error:', error);
                this.saving = false;
                this.errorMessage = 'User info saved, but role update failed.';
              },
            });
        },
        error: (error) => {
          console.error('Update user error:', error);
          this.saving = false;
          this.errorMessage = 'Failed to update user.';
        },
      });
  }

  promoteToAdmin(user: AdminUser): void {
    this.changeRole(user, 'ADMIN');
  }

  demoteToUser(user: AdminUser): void {
    this.changeRole(user, 'USER');
  }

  changeRole(user: AdminUser, role: 'ADMIN' | 'ANALYST' | 'USER'): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.adminUserService.updateUserRole(user.id, { role }).subscribe({
      next: () => {
        this.successMessage = `${user.email} role changed to ${role}.`;
        this.loadUsers();
      },
      error: (error) => {
        console.error('Change role error:', error);
        this.errorMessage = 'Failed to update user role.';
      },
    });
  }

  toggleStatus(user: AdminUser): void {
    this.errorMessage = '';
    this.successMessage = '';

    const request = user.active
      ? this.adminUserService.disableUser(user.id)
      : this.adminUserService.enableUser(user.id);

    request.subscribe({
      next: () => {
        this.successMessage = user.active
          ? `${user.email} has been disabled.`
          : `${user.email} has been enabled.`;
        this.loadUsers();
      },
      error: (error) => {
        console.error('Toggle status error:', error);
        this.errorMessage = 'Failed to change user status.';
      },
    });
  }

  deleteUser(user: AdminUser): void {
    const currentUserId = Number(localStorage.getItem('userId'));

    if (user.id === currentUserId) {
      this.errorMessage = 'You cannot delete your own account while logged in.';
      return;
    }

    const confirmed = confirm(
      `Are you sure you want to delete user ${user.email}?`
    );

    if (!confirmed) {
      return;
    }

    this.adminUserService.deleteUser(user.id).subscribe({
      next: () => {
        this.successMessage = `${user.email} deleted successfully.`;
        this.loadUsers();
      },
      error: (error) => {
        console.error('Delete user error:', error);
        this.errorMessage = 'Failed to delete user.';
      },
    });
  }

  getTotalUsers(): number {
    return this.users.length;
  }

  getActiveUsers(): number {
    return this.users.filter((user) => user.active).length;
  }

  getAdminUsers(): number {
    return this.users.filter((user) => user.role === 'ADMIN').length;
  }

  getDisabledUsers(): number {
    return this.users.filter((user) => !user.active).length;
  }

  getRoleClass(role: string): string {
    if (role === 'ADMIN') return 'danger';
    if (role === 'ANALYST') return 'warning';
    return 'primary';
  }
}