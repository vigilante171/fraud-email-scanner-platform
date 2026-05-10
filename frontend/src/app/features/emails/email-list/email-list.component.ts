import { Component, OnInit } from '@angular/core';
import { ScannerService } from '../../../core/services/scanner.service';

@Component({
  selector: 'app-email-list',
  templateUrl: './email-list.component.html',
  styleUrls: ['./email-list.component.scss']
})
export class EmailListComponent implements OnInit {
  emails: any[] = [];
  filteredEmails: any[] = [];

  loading = false;
  errorMessage = '';
  searchTerm = '';

  constructor(private scannerService: ScannerService) {}

  ngOnInit(): void {
    this.loadFlaggedEmails();
  }

  loadFlaggedEmails(): void {
    this.loading = true;
    this.errorMessage = '';

    this.scannerService.getFlaggedEmails().subscribe({
      next: (data) => {
        this.emails = data || [];
        this.filteredEmails = this.emails;
        this.loading = false;
      },
      error: (error) => {
        console.error('Flagged emails error:', error);
        this.errorMessage = 'Failed to load flagged emails.';
        this.loading = false;
      }
    });
  }

  filterEmails(): void {
    const term = this.searchTerm.toLowerCase().trim();

    if (!term) {
      this.filteredEmails = this.emails;
      return;
    }

    this.filteredEmails = this.emails.filter((email) => {
      const text = JSON.stringify(email).toLowerCase();
      return text.includes(term);
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