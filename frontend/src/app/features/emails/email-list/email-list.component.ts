import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ScannerService } from '../../../core/services/scanner.service';

@Component({
  selector: 'app-email-list',
  templateUrl: './email-list.component.html',
  styleUrls: ['./email-list.component.scss'],
})
export class EmailListComponent implements OnInit {
  emails: any[] = [];
  filteredEmails: any[] = [];

  loading = false;
  deleting = false;

  errorMessage = '';
  successMessage = '';
  searchTerm = '';

  showDeleteModal = false;
  selectedEmailToDelete: any = null;

  constructor(
    private scannerService: ScannerService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(() => {
      this.loadFlaggedEmails();
    });
  }

  loadFlaggedEmails(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.scannerService.getFlaggedEmails().subscribe({
      next: (data) => {
        this.emails = data || [];
        this.applyCurrentFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Flagged emails error:', error);
        this.errorMessage = 'Failed to load flagged emails.';
        this.loading = false;
      },
    });
  }

  filterEmails(): void {
    this.applyCurrentFilter();
  }

  applyCurrentFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();

    if (!term) {
      this.filteredEmails = [...this.emails];
      return;
    }

    this.filteredEmails = this.emails.filter((email) => {
      const text = JSON.stringify(email).toLowerCase();
      return text.includes(term);
    });
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  openDeleteModal(email: any): void {
    if (!this.isAdmin()) {
      return;
    }

    this.selectedEmailToDelete = email;
    this.showDeleteModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeDeleteModal(): void {
    if (this.deleting) {
      return;
    }

    this.showDeleteModal = false;
    this.selectedEmailToDelete = null;
  }

  confirmDeleteEmail(): void {
    if (!this.selectedEmailToDelete) {
      return;
    }

    const emailId = this.getId(this.selectedEmailToDelete);

    if (!emailId) {
      this.errorMessage = 'Cannot delete email: invalid email ID.';
      this.closeDeleteModal();
      return;
    }

    this.deleting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.scannerService.deleteEmail(emailId).subscribe({
      next: () => {
        this.emails = this.emails.filter((email) => this.getId(email) !== emailId);
        this.applyCurrentFilter();

        this.successMessage = `Email #${emailId} deleted successfully.`;
        this.deleting = false;
        this.showDeleteModal = false;
        this.selectedEmailToDelete = null;
      },
      error: (error) => {
        console.error('Delete email error:', error);
        this.errorMessage = 'Failed to delete email.';
        this.deleting = false;
        this.showDeleteModal = false;
      },
    });
  }

  getId(email: any): number {
    return email.id || email.emailId || email.emailMessageId;
  }

  getSender(email: any): string {
    return email.sender || email.senderEmail || email.from || 'Unknown sender';
  }

  getSubject(email: any): string {
    return email.subject || 'No subject';
  }

  getStatus(email: any): string {
    return email.status || email.emailStatus || 'FLAGGED';
  }

  getRiskLevel(email: any): string {
    return email.riskLevel || 'HIGH';
  }

  getRiskScore(email: any): number {
    return email.riskScore ?? 0;
  }

  getDate(email: any): string {
    return email.receivedAt || email.scannedAt || email.createdAt || '';
  }

  getStatusClass(status: string): string {
    const value = (status || '').toUpperCase();

    if (value === 'FLAGGED') return 'status-flagged';
    if (value === 'SUSPICIOUS') return 'status-suspicious';
    if (value === 'SAFE') return 'status-safe';

    return 'status-neutral';
  }
}