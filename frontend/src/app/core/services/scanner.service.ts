import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EmailScanRequest {
  sender: string;
  receiverEmail: string;
  subject: string;
  body: string;
  receivedAt: string;
  userId?: number;
}

export interface ScanResponse {
  emailId: number;
  scanId: number;
  status: string;
  riskLevel: string;
  riskScore: number;
  reasons: string[];
  scannedAt: string;
  mlFraudProbability?: number;
  mlPrediction?: string;
  mlRiskLevel?: string;
  mlModelVersion?: string;
  mlReasons?: string[];
  ruleRiskScore?: number;
  finalRiskScore?: number;
}

export interface FeedbackRequest {
  emailId: number;
  userId?: number | null;
  label: string;
  comment?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ScannerService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Existing email‑related methods
  scanEmail(payload: EmailScanRequest): Observable<ScanResponse> {
    return this.http.post<ScanResponse>(
      `${this.apiUrl}/api/emails/scan`,
      payload,
    );
  }

  getAllEmails(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails`, {
      params: this.getUserAccessParams(),
    });
  }

  getFlaggedEmails(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails/flagged`, {
      params: this.getUserAccessParams(),
    });
  }

  getEmailDetails(emailId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/emails/${emailId}`, {
      params: this.getUserAccessParams(),
    });
  }

  getEmailsByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails/status/${status}`, {
      params: this.getUserAccessParams(),
    });
  }

  submitFeedback(payload: FeedbackRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/api/feedback`, payload, {
      responseType: 'text',
    });
  }

  // 🆕 Admin‑level endpoints (parallel to admin routes)
  getAuditLogs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/audit-logs`, {
      params: this.getUserAccessParams(),
    });
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/admin/users`, {
      params: this.getUserAccessParams(),
    });
  }

  private getUserAccessParams(): HttpParams {
    const userId = localStorage.getItem('userId');
    const role = localStorage.getItem('role') || 'USER';
    let params = new HttpParams().set('role', role);
    if (userId) params = params.set('userId', userId);
    return params;
  }
}
