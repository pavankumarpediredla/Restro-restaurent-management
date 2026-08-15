import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AttendanceStatusCell } from '../attendance-status-cell/attendance-status-cell';
import { AttendanceReportEmployee, ReportStatusCode } from '../attendance-report.types';
@Component({ selector: 'app-attendance-report-table', standalone: true, imports: [AttendanceStatusCell], templateUrl: './attendance-report-table.html', styleUrl: './attendance-report-table.scss' })
export class AttendanceReportTable {
  @Input({ required: true }) month = '';
  @Input() employees: AttendanceReportEmployee[] = [];
  @Input() firstIndex = 0;
  @Output() manage = new EventEmitter<AttendanceReportEmployee>();
  get dates(): Date[] { const [year, month] = this.month.split('-').map(Number); const count = new Date(year, month, 0).getDate(); return Array.from({ length: count }, (_, index) => new Date(year, month - 1, index + 1)); }
  key(date: Date): string { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`; }
  weekday(date: Date): string { return date.toLocaleDateString(undefined, { weekday: 'short' }); }
  initials(employee: AttendanceReportEmployee): string { return employee.name.split(/\s+/).map(part => part[0]).join('').slice(0, 2).toUpperCase(); }
  status(employee: AttendanceReportEmployee, date: Date): ReportStatusCode { return employee.attendance.get(this.key(date)) ?? 'NA'; }
}
