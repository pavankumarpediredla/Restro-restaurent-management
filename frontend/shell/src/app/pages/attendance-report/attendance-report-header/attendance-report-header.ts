import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({ selector: 'app-attendance-report-header', standalone: true, imports: [FormsModule], templateUrl: './attendance-report-header.html', styleUrl: './attendance-report-header.scss' })
export class AttendanceReportHeader {
  @Input({ required: true }) month = '';
  @Output() monthChange = new EventEmitter<string>();
}
