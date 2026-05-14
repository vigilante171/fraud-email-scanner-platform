import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminGuard } from './core/guards/admin.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { ScanEmailComponent } from './features/scanner/scan-email/scan-email.component';
import { EmailListComponent } from './features/emails/email-list/email-list.component';
import { EmailDetailsComponent } from './features/emails/email-details/email-details.component';
import { AuditLogsComponent } from './features/audit/audit-logs/audit-logs.component';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'scan-email',
    component: ScanEmailComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'emails',
    component: EmailListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'emails/:id',
    component: EmailDetailsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'audit-logs',
    component: AuditLogsComponent,
    canActivate: [AuthGuard, AdminGuard],
  },

  { path: '**', redirectTo: 'dashboard' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
