import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ScannerService,
  UpdateEmailRequest,
} from '../../../core/services/scanner.service';

@Component({
  selector: 'app-email-details',
  templateUrl: './email-details.component.html',
  styleUrls: ['./email-details.component.scss'],
})
export class EmailDetailsComponent implements OnInit {
  email: any = null;

  loading = false;
  errorMessage = '';
  successMessage = '';

  feedbackLoading = false;
  feedbackSuccessMessage = '';
  feedbackErrorMessage = '';
  feedbackComment = '';

  editMode = false;
  editLoading = false;
  deleteLoading = false;
  showDeleteModal = false;

  editForm: UpdateEmailRequest = {
    sender: '',
    receiverEmail: '',
    subject: '',
    body: '',
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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
    this.successMessage = '';

    this.scannerService.getEmailDetails(emailId).subscribe({
      next: (data) => {
        this.email = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Email details error:', error);

        if (error.status === 404 || error.status === 500) {
          this.errorMessage =
            'This email no longer exists or its scan result was deleted.';
        } else {
          this.errorMessage = 'Failed to load email details.';
        }

        this.loading = false;
      },
    });
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  startEdit(): void {
    if (!this.isAdmin()) {
      return;
    }

    this.editMode = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.editForm = {
      sender: this.value('sender', 'senderEmail', 'email.sender'),
      receiverEmail: this.value('receiverEmail', 'email.receiverEmail'),
      subject: this.value('subject', 'email.subject'),
      body: this.value('body', 'email.body'),
    };
  }

  cancelEdit(): void {
    this.editMode = false;
    this.editLoading = false;
  }

  saveEmailChanges(): void {
    const emailId = Number(this.value('id', 'emailId', 'email.id'));

    if (!emailId) {
      this.errorMessage = 'Cannot update email: invalid email ID.';
      return;
    }

    this.editLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.scannerService.updateEmail(emailId, this.editForm).subscribe({
      next: (updatedEmail) => {
        this.email = updatedEmail;
        this.editMode = false;
        this.editLoading = false;
        this.successMessage = 'Email updated successfully.';
      },
      error: (error) => {
        console.error('Update email error:', error);
        this.errorMessage =
          'Failed to update email. Admin permission may be missing.';
        this.editLoading = false;
      },
    });
  }

  openDeleteModal(): void {
    if (!this.isAdmin()) {
      return;
    }

    this.showDeleteModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeDeleteModal(): void {
    if (this.deleteLoading) {
      return;
    }

    this.showDeleteModal = false;
  }

  confirmDeleteEmail(): void {
    const emailId = Number(this.value('id', 'emailId', 'email.id'));

    if (!emailId) {
      this.errorMessage = 'Cannot delete email: invalid email ID.';
      this.showDeleteModal = false;
      return;
    }

    this.deleteLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.scannerService.deleteEmail(emailId).subscribe({
      next: () => {
        this.deleteLoading = false;
        this.showDeleteModal = false;

        this.router.navigate(['/emails'], {
          queryParams: {
            refresh: Date.now(),
          },
        });
      },
      error: (error) => {
        console.error('Delete email error:', error);
        this.errorMessage =
          'Failed to delete email. Admin permission may be missing.';
        this.deleteLoading = false;
        this.showDeleteModal = false;
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
        userId: Number(localStorage.getItem('userId')) || null,
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
