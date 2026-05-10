import { Component } from '@angular/core';
import { ScannerService, ScanResponse } from '../../../core/services/scanner.service';

@Component({
  selector: 'app-scan-email',
  templateUrl: './scan-email.component.html',
  styleUrls: ['./scan-email.component.scss']
})
export class ScanEmailComponent {
  form = {
    sender: '',
    receiverEmail: '',
    subject: '',
    body: ''
  };

  loading = false;
  errorMessage = '';
  result: ScanResponse | null = null;

  constructor(private scannerService: ScannerService) {}

  scanEmail(): void {
    this.errorMessage = '';
    this.result = null;

    if (!this.form.sender || !this.form.receiverEmail || !this.form.subject || !this.form.body) {
      this.errorMessage = 'Please fill all fields before scanning.';
      return;
    }

    this.loading = true;

    const payload = {
      ...this.form,
      receivedAt: this.getCurrentLocalDateTime()
    };

    this.scannerService.scanEmail(payload).subscribe({
      next: (response) => {
        this.result = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Scan error:', error);
        this.errorMessage = 'Failed to scan email. Check scanner-service and ml-service logs.';
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.form = {
      sender: '',
      receiverEmail: '',
      subject: '',
      body: ''
    };

    this.result = null;
    this.errorMessage = '';
  }

  getMlProbabilityPercent(): number {
    if (!this.result?.mlFraudProbability) {
      return 0;
    }

    return Math.round(this.result.mlFraudProbability * 100);
  }

  getScoreWidth(score?: number): string {
    const value = score ?? 0;
    return `${Math.min(Math.max(value, 0), 100)}%`;
  }

  getRiskClass(value?: string): string {
    const risk = (value || '').toUpperCase();

    if (risk.includes('HIGH') || risk.includes('FRAUD')) return 'danger';
    if (risk.includes('MEDIUM') || risk.includes('SUSPICIOUS')) return 'warning';
    if (risk.includes('LOW') || risk.includes('SAFE')) return 'success';

    return 'neutral';
  }

  getStatusClass(value?: string): string {
    const status = (value || '').toUpperCase();

    if (status === 'FLAGGED') return 'danger';
    if (status === 'SUSPICIOUS') return 'warning';
    if (status === 'SAFE') return 'success';

    return 'neutral';
  }

  private getCurrentLocalDateTime(): string {
    const now = new Date();
    const pad = (value: number) => value.toString().padStart(2, '0');

    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
  }
}