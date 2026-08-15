import { Component, EventEmitter, Input, Output } from '@angular/core';
export type AttendanceReportTab = 'summary' | 'detail';
@Component({ selector: 'app-attendance-report-tabs', standalone: true, templateUrl: './attendance-report-tabs.html', styleUrl: './attendance-report-tabs.scss' })
export class AttendanceReportTabs { @Input() active: AttendanceReportTab = 'detail'; @Output() activeChange = new EventEmitter<AttendanceReportTab>(); }
