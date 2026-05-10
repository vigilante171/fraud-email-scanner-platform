import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EmailScanRequest {
  sender: string;
  receiverEmail: string;
  subject: string;
  body: string;
  receivedAt: string;
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

@Injectable({
  providedIn: 'root'
})
export class ScannerService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  scanEmail(payload: EmailScanRequest): Observable<ScanResponse> {
    return this.http.post<ScanResponse>(`${this.apiUrl}/api/emails/scan`, payload);
  }

  getAllEmails(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails`);
  }

  getFlaggedEmails(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails/flagged`);
  }

  getEmailDetails(emailId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/emails/${emailId}`);
  }

  getEmailsByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/emails/status/${status}`);
  }
}