import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface StatisticsSummary {
  totalEmails?: number;
  totalScans?: number;
  flaggedEmails?: number;
  suspiciousEmails?: number;
  safeEmails?: number;
  averageRiskScore?: number;
  totalFeedbacks?: number;
}

export interface DistributionItem {
  label?: string;
  name?: string;
  status?: string;
  riskLevel?: string;
  count: number;
}

export interface TopSender {
  sender?: string;
  senderEmail?: string;
  email?: string;
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<StatisticsSummary> {
    return this.http.get<StatisticsSummary>(`${this.apiUrl}/api/statistics/summary`);
  }

  getStatusDistribution(): Observable<DistributionItem[]> {
    return this.http.get<DistributionItem[]>(`${this.apiUrl}/api/statistics/status-distribution`);
  }

  getRiskDistribution(): Observable<DistributionItem[]> {
    return this.http.get<DistributionItem[]>(`${this.apiUrl}/api/statistics/risk-distribution`);
  }

  getTopFlaggedSenders(): Observable<TopSender[]> {
    return this.http.get<TopSender[]>(`${this.apiUrl}/api/statistics/top-flagged-senders`);
  }
}