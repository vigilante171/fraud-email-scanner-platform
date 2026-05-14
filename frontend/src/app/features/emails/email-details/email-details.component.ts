import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ScannerService } from '../../../core/services/scanner.service';

@Component({
  selector: 'app-email-details',
  templateUrl: './email-details.component.html',
  styleUrls: ['./email-details.component.scss'],
})
export class EmailDetailsComponent implements OnInit {
  email: any = null;

  loading = false;
  errorMessage = '';

  feedbackLoading = false;
  feedbackSuccessMessage = '';
  feedbackErrorMessage = '';
  feedbackComment = '';

  constructor(
    private route: ActivatedRoute,
    private scannerService: ScannerService,
  ) {}

  ngOnInit(): void {
    const emailId = Number(this.route.snapshot.paramMap.get('id'));

    if (!emailId) {
      this.errorMessage = 'Invalid email ID.';
      return;
    }

    this.loadEmailDetails(emailId);
  }

  loadEmailDetails(emailId: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.scannerService.getEmailDetails(emailId).subscribe({
      next: (data) => {
        this.email = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Email details error:', error);
        this.errorMessage = 'Failed to load email details.';
        this.loading = false;
      },
    });
  }

  submitFeedback(label: 'FALSE_POSITIVE' | 'CONFIRMED_FRAUD'): void {
    const emailId = Number(this.value('id', 'emailId', 'email.id'));

    if (!emailId) {
      this.feedbackErrorMessage = 'Cannot submit feedback: invalid email ID.';
      return;
    }

    this.feedbackLoading = true;
    this.feedbackSuccessMessage = '';
    this.feedbackErrorMessage = '';

    this.scannerService
      .submitFeedback({
        emailId,
        userId: null,
        label,
        comment: this.feedbackComment,
      })
      .subscribe({
        next: (response) => {
          this.feedbackLoading = false;
          this.feedbackSuccessMessage =
            response || `Feedback submitted successfully: ${label}`;
          this.feedbackComment = '';
        },
        error: (error) => {
          console.error('Feedback error:', error);
          this.feedbackLoading = false;
          this.feedbackErrorMessage =
            'Failed to submit feedback. Check scanner-service logs.';
        },
      });
  }

  value(...keys: string[]): any {
    for (const key of keys) {
      const found = this.getNestedValue(this.email, key);

      if (found !== undefined && found !== null && found !== '') {
        return found;
      }
    }

    return 'N/A';
  }

  reasons(): string[] {
    const raw = this.value('reasons', 'scanResult.reasons', 'detectionReasons');

    if (Array.isArray(raw)) {
      return raw;
    }

    if (typeof raw === 'string') {
      return raw
        .split('|')
        .map((item) => item.trim())
        .filter(Boolean);
    }

    return [];
  }

  getStatusClass(): string {
    const status = String(
      this.value('status', 'scanResult.status'),
    ).toUpperCase();

    if (status === 'FLAGGED') return 'status-flagged';
    if (status === 'SUSPICIOUS') return 'status-suspicious';
    if (status === 'SAFE') return 'status-safe';

    return 'status-neutral';
  }

  getRiskScore(): number {
    const score = Number(this.value('riskScore', 'scanResult.riskScore'));
    return Number.isNaN(score) ? 0 : score;
  }

  getFeedbackHint(): string {
    const status = String(
      this.value('status', 'scanResult.status'),
    ).toUpperCase();

    if (status === 'FLAGGED' || status === 'SUSPICIOUS') {
      return 'Confirm whether this detection is truly fraudulent or mark it as a false positive.';
    }

    return 'If this email is actually fraudulent, confirm it so the system can keep track of detection quality.';
  }

  private getNestedValue(obj: any, path: string): any {
    if (!obj) {
      return undefined;
    }

    return path.split('.').reduce((current, key) => {
      return current ? current[key] : undefined;
    }, obj);
  }
}
