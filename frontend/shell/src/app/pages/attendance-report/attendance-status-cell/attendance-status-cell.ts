import { Component, Input } from '@angular/core';
import { attendanceStatusConfig, ReportStatusCode } from '../attendance-report.types';
@Component({ selector: 'app-attendance-status-cell', standalone: true, templateUrl: './attendance-status-cell.html', styleUrl: './attendance-status-cell.scss' })
export class AttendanceStatusCell { @Input({ required: true }) status: ReportStatusCode = 'NA'; get config() { return attendanceStatusConfig[this.status]; } }
