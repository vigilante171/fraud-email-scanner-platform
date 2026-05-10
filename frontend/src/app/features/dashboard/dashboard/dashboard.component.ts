import {
  AfterViewInit,
  Component,
  OnDestroy
} from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import {
  DistributionItem,
  StatisticsService,
  StatisticsSummary,
  TopSender
} from '../../../core/services/statistics.service';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit, OnDestroy {
  summary: StatisticsSummary | null = null;
  statusDistribution: DistributionItem[] = [];
  riskDistribution: DistributionItem[] = [];
  topSenders: TopSender[] = [];

  loading = true;
  errorMessage = '';

  private statusChart?: Chart;
  private riskChart?: Chart;

  constructor(private statisticsService: StatisticsService) {}

  ngAfterViewInit(): void {
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.statusChart?.destroy();
    this.riskChart?.destroy();
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';

    this.statisticsService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
      },
      error: (error) => {
        console.error('Summary error:', error);
        this.errorMessage = 'Failed to load dashboard summary.';
      }
    });

    this.statisticsService.getStatusDistribution().subscribe({
      next: (data) => {
        this.statusDistribution = data || [];
        this.renderStatusChart();
      },
      error: (error) => {
        console.error('Status distribution error:', error);
      }
    });

    this.statisticsService.getRiskDistribution().subscribe({
      next: (data) => {
        this.riskDistribution = data || [];
        this.renderRiskChart();
      },
      error: (error) => {
        console.error('Risk distribution error:', error);
      }
    });

    this.statisticsService.getTopFlaggedSenders().subscribe({
      next: (data) => {
        this.topSenders = data || [];
        this.loading = false;
      },
      error: (error) => {
        console.error('Top senders error:', error);
        this.loading = false;
      }
    });
  }

  getMetric(value: number | undefined | null): number {
    return value ?? 0;
  }

  getAverageRisk(): string {
    const value = this.summary?.averageRiskScore ?? 0;
    return Number(value).toFixed(1);
  }

  getDistributionLabel(item: DistributionItem): string {
    return item.label || item.name || item.status || item.riskLevel || 'Unknown';
  }

  getSenderName(sender: TopSender): string {
    return sender.sender || sender.senderEmail || sender.email || 'Unknown sender';
  }

  private renderStatusChart(): void {
    setTimeout(() => {
      const canvas = document.getElementById('statusChart') as HTMLCanvasElement | null;

      if (!canvas) {
        return;
      }

      this.statusChart?.destroy();

      const labels = this.statusDistribution.map((item) => this.getDistributionLabel(item));
      const values = this.statusDistribution.map((item) => item.count);

      const config: ChartConfiguration = {
        type: 'doughnut',
        data: {
          labels,
          datasets: [
            {
              data: values,
              borderWidth: 2
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom'
            }
          }
        }
      };

      this.statusChart = new Chart(canvas, config);
    });
  }

  private renderRiskChart(): void {
    setTimeout(() => {
      const canvas = document.getElementById('riskChart') as HTMLCanvasElement | null;

      if (!canvas) {
        return;
      }

      this.riskChart?.destroy();

      const labels = this.riskDistribution.map((item) => this.getDistributionLabel(item));
      const values = this.riskDistribution.map((item) => item.count);

      const config: ChartConfiguration = {
        type: 'bar',
        data: {
          labels,
          datasets: [
            {
              label: 'Emails',
              data: values,
              borderWidth: 1
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0
              }
            }
          }
        }
      };

      this.riskChart = new Chart(canvas, config);
    });
  }
}