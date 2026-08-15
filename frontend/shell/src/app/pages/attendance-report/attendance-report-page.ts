import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { AttendanceReportFilters, AttendanceReportEmployee, attendanceStatusConfig, ReportStatusCode } from './attendance-report.types';
import { AttendanceReportService } from './attendance-report.service';
import { AttendanceReportHeader } from './attendance-report-header/attendance-report-header';
import { AttendanceReportTabs, AttendanceReportTab } from './attendance-report-tabs/attendance-report-tabs';
import { AttendanceReportFiltersComponent } from './attendance-report-filters/attendance-report-filters';
import { AttendanceReportTable } from './attendance-report-table/attendance-report-table';
import { AttendanceStatusLegend } from './attendance-status-legend/attendance-status-legend';
import { AttendanceReportPagination } from './attendance-report-pagination/attendance-report-pagination';
import { StaffAttendanceCalendar } from '../staff-attendance-calendar/staff-attendance-calendar';

@Component({
  selector: 'app-attendance-report-page', standalone: true,
  imports: [CommonModule, AttendanceReportHeader, AttendanceReportTabs, AttendanceReportFiltersComponent, AttendanceReportTable, AttendanceStatusLegend, AttendanceReportPagination, StaffAttendanceCalendar],
  templateUrl: './attendance-report-page.html', styleUrl: './attendance-report-page.scss',
})
export class AttendanceReportPage implements OnInit {
  month = this.monthKey(new Date()); activeTab: AttendanceReportTab = 'detail'; employees: AttendanceReportEmployee[] = [];
  filters: AttendanceReportFilters = this.emptyFilters(); page = 1; pageSize = 6; loading = false; error = ''; success = ''; showFilters = false; importing = false; selectedEmployee: AttendanceReportEmployee | null = null;
  constructor(private report: AttendanceReportService, private api: ApiService) {}
  ngOnInit(): void { void this.load(); }
  get filteredEmployees(): AttendanceReportEmployee[] { return this.employees.filter(employee => {
    const selectedEmployee = !this.filters.employee || employee.staffId === Number(this.filters.employee);
    const selectedStatus = this.filters.employeeStatus === 'ALL' || (this.filters.employeeStatus === 'ACTIVE' ? employee.employeeStatus === 'Active' : employee.employeeStatus === 'Ex-Employee');
    const selectedAttendance = this.filters.attendanceStatus === 'ALL' || [...employee.attendance.values()].includes(this.filters.attendanceStatus);
    return selectedEmployee && selectedStatus && selectedAttendance;
  }); }
  get visibleEmployees(): AttendanceReportEmployee[] { return this.filteredEmployees.slice((this.page - 1) * this.pageSize, this.page * this.pageSize); }
  get summary(): Array<{ code: ReportStatusCode; count: number }> { return (Object.keys(attendanceStatusConfig) as ReportStatusCode[]).map(code => ({ code, count: this.employees.reduce((total, employee) => total + [...employee.attendance.values()].filter(status => status === code).length, 0) })); }
  async load(): Promise<void> { this.loading = true; this.error = ''; try { this.employees = (await this.report.load(this.month)).employees; this.page = 1; } catch (error) { this.error = this.message(error); } finally { this.loading = false; } }
  async changeMonth(month: string): Promise<void> { this.month = month; await this.load(); }
  applyFilters(filters: AttendanceReportFilters): void { this.filters = { ...filters }; this.page = 1; }
  resetFilters(): void { this.filters = this.emptyFilters(); this.page = 1; }
  exportCsv(): void { const days = this.days(); const rows = this.filteredEmployees.map(employee => [employee.name, employee.code, employee.department, employee.employeeStatus, ...days.map(day => employee.attendance.get(day) ?? 'NA')]); this.download(`attendance-report-${this.month}.csv`, [['Employee', 'Employee code', 'Department', 'Employee status', ...days], ...rows].map(row => row.map(this.csv).join(',')).join('\n'), 'text/csv'); }
  downloadTemplate(): void { this.download('attendance-import-template.csv', 'date,employeeCode,status,note\n2026-08-01,employee.username,P,On time\n', 'text/csv'); }
  async importFile(event: Event): Promise<void> { const input = event.target as HTMLInputElement; const file = input.files?.[0]; if (!file) return; this.importing = true; this.error = ''; this.success = ''; try { const text = await file.text(); const lines = text.split(/\r?\n/).slice(1).filter(Boolean); const staffByCode = new Map(this.employees.map(employee => [employee.code, employee.staffId])); const groups = new Map<string, Array<{ staffId: number; status: string; note: string }>>(); for (const line of lines) { const [date, code, rawStatus, note = ''] = line.split(',').map(value => value.trim()); const staffId = staffByCode.get(code); const status = this.apiStatus(rawStatus); if (!date || !staffId || !status) throw new Error(`Invalid import row: ${line}`); groups.set(date, [...(groups.get(date) ?? []), { staffId, status, note }]); } for (const [attendanceDate, records] of groups) await this.api.post('/api/attendance', { attendanceDate, records }); this.success = 'Attendance import completed.'; await this.load(); } catch (error) { this.error = this.message(error); } finally { this.importing = false; input.value = ''; } }
  private apiStatus(code: string): string | null { return ({ P: 'PRESENT', A: 'ABSENT', L: 'LATE', H: 'HOLIDAY', WO: 'WEEK_OFF', LWP: 'LEAVE_WITHOUT_PAY', NA: 'NOT_AVAILABLE' } as Record<string, string>)[code.toUpperCase()] ?? null; }
  private days(): string[] { const [year, month] = this.month.split('-').map(Number); return Array.from({ length: new Date(year, month, 0).getDate() }, (_, index) => `${this.month}-${String(index + 1).padStart(2, '0')}`); }
  private csv(value: unknown): string { return `"${String(value ?? '').replaceAll('"', '""')}"`; }
  private download(name: string, content: string, type: string): void { const url = URL.createObjectURL(new Blob([content], { type })); const anchor = document.createElement('a'); anchor.href = url; anchor.download = name; anchor.click(); URL.revokeObjectURL(url); }
  private monthKey(date: Date): string { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`; }
  private emptyFilters(): AttendanceReportFilters { return { employee: '', employeeStatus: 'ALL', attendanceStatus: 'ALL' }; }
  private message(error: unknown): string { return error instanceof Error ? error.message : String(error); }
}
