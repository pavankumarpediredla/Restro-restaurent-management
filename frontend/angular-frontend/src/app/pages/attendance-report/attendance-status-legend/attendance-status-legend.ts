import { Component } from '@angular/core';
import { AttendanceStatusCell } from '../attendance-status-cell/attendance-status-cell';
import { attendanceStatusConfig, ReportStatusCode } from '../attendance-report.types';
@Component({ selector: 'app-attendance-status-legend', standalone: true, imports: [AttendanceStatusCell], templateUrl: './attendance-status-legend.html', styleUrl: './attendance-status-legend.scss' })
export class AttendanceStatusLegend { statuses = Object.keys(attendanceStatusConfig) as ReportStatusCode[]; config = attendanceStatusConfig; }
