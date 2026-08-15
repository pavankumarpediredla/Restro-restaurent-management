import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AttendanceReportFilters, attendanceStatusConfig, ReportStatusCode } from '../attendance-report.types';
@Component({ selector: 'app-attendance-report-filters', standalone: true, imports: [FormsModule], templateUrl: './attendance-report-filters.html', styleUrl: './attendance-report-filters.scss' })
export class AttendanceReportFiltersComponent {
  @Input({ required: true }) filters!: AttendanceReportFilters;
  @Input() employees: Array<{ name: string; staffId: number }> = [];
  @Output() apply = new EventEmitter<AttendanceReportFilters>(); @Output() reset = new EventEmitter<void>(); @Output() close = new EventEmitter<void>();
  statuses = Object.keys(attendanceStatusConfig) as ReportStatusCode[]; config = attendanceStatusConfig;
  draft(): AttendanceReportFilters { return { ...this.filters }; }
  applyFilters(): void { this.apply.emit(this.filters); }
}
