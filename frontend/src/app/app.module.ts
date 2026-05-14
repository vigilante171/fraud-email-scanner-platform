import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LayoutComponent } from './shared/layout/layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { ScanEmailComponent } from './features/scanner/scan-email/scan-email.component';
import { EmailListComponent } from './features/emails/email-list/email-list.component';
import { EmailDetailsComponent } from './features/emails/email-details/email-details.component';
import { AuditLogsComponent } from './features/audit/audit-logs/audit-logs.component';

import { AuthTokenInterceptor } from './core/interceptors/auth-token.interceptor';
import { UserManagementComponent } from './features/admin/user-management/user-management.component';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    ScanEmailComponent,
    EmailListComponent,
    EmailDetailsComponent,
    AuditLogsComponent,
    UserManagementComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthTokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
