import { Component, OnInit } from '@angular/core';
import { AuditLog, AuditService } from '../../../core/services/audit.service';

@Component({
  selector: 'app-audit-logs',
  templateUrl: './audit-logs.component.html',
  styleUrls: ['./audit-logs.component.scss']
})
export class AuditLogsComponent implements OnInit {
  logs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];

  loading = false;
  errorMessage = '';

  searchTerm = '';
  selectedEventType = 'ALL';

  eventTypes: string[] = [];

  constructor(private auditService: AuditService) {}

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs(): void {
    this.loading = true;
    this.errorMessage = '';

    this.auditService.getAllAuditLogs().subscribe({
      next: (logs) => {
        this.logs = this.sortLogsByDate(logs || []);
        this.filteredLogs = this.logs;

        this.eventTypes = Array.from(
          new Set(
            this.logs
              .map((log) => this.getEventType(log))
              .filter((type) => type !== 'N/A')
          )
        );

        this.loading = false;
      },
      error: (error) => {
        console.error('Audit logs error:', error);
        this.errorMessage =
          'Failed to load audit logs. Make sure audit-service is running on port 8083.';
        this.loading = false;
      }
    });
  }

  filterLogs(): void {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredLogs = this.logs.filter((log) => {
      const matchesSearch =
        !term ||
        this.getEventType(log).toLowerCase().includes(term) ||
        String(this.getEmailId(log)).toLowerCase().includes(term) ||
        String(this.getUserId(log)).toLowerCase().includes(term) ||
        this.getPerformedBy(log).toLowerCase().includes(term) ||
        this.getDetails(log).toLowerCase().includes(term);

      const matchesEventType =
        this.selectedEventType === 'ALL' ||
        this.getEventType(log) === this.selectedEventType;

      return matchesSearch && matchesEventType;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedEventType = 'ALL';
    this.filteredLogs = this.logs;
  }

  getTotalEvents(): number {
    return this.logs.length;
  }

  getScannedEvents(): number {
    return this.logs.filter((log) =>
      this.getEventType(log).includes('SCANNED')
    ).length;
  }

  getFlaggedEvents(): number {
    return this.logs.filter((log) =>
      this.getEventType(log).includes('FLAGGED')
    ).length;
  }

  getLatestEventDate(): string {
    if (!this.logs.length) {
      return '';
    }

    return this.getCreatedAt(this.logs[0]);
  }

  getEmailId(log: AuditLog): number | string {
    return log.emailId ?? 'N/A';
  }

  hasEmailId(log: AuditLog): boolean {
    return log.emailId !== undefined && log.emailId !== null;
  }

  getUserId(log: AuditLog): number | string {
    return log.userId ?? 'System';
  }

  getEventType(log: AuditLog): string {
    return log.eventType || 'N/A';
  }

  getPerformedBy(log: AuditLog): string {
    return log.performedBy || 'N/A';
  }

  getDetails(log: AuditLog): string {
    return log.details || 'No details available.';
  }

  getCreatedAt(log: AuditLog): string {
    return log.createdAt || '';
  }

  getEventClass(log: AuditLog): string {
    const type = this.getEventType(log).toUpperCase();

    if (type.includes('FLAGGED')) {
      return 'danger';
    }

    if (type.includes('SCANNED')) {
      return 'primary';
    }

    if (type.includes('LOGIN') || type.includes('REGISTER')) {
      return 'success';
    }

    if (type.includes('FEEDBACK')) {
      return 'warning';
    }

    return 'neutral';
  }

  private sortLogsByDate(logs: AuditLog[]): AuditLog[] {
    return [...logs].sort((a, b) => {
      const dateA = new Date(this.getCreatedAt(a)).getTime() || 0;
      const dateB = new Date(this.getCreatedAt(b)).getTime() || 0;

      return dateB - dateA;
    });
  }
}