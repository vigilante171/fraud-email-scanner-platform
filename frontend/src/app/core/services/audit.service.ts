import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuditLog {
  id: number;
  emailId: number | null;
  userId: number | null;
  eventType: string;
  performedBy: string;
  details: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuditService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllAuditLogs(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.apiUrl}/api/audit`);
  }

  getAuditLogsByEmailId(emailId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(
      `${this.apiUrl}/api/audit/email/${emailId}`,
    );
  }

  getAuditLogsByEventType(eventType: string): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(
      `${this.apiUrl}/api/audit/event/${eventType}`,
    );
  }

  checkHealth(): Observable<string> {
    return this.http.get(`${this.apiUrl}/api/audit/health`, {
      responseType: 'text',
    });
  }
}
