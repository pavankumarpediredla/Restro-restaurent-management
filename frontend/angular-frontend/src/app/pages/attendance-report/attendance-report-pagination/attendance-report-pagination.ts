import { Component, EventEmitter, Input, Output } from '@angular/core';
@Component({ selector: 'app-attendance-report-pagination', standalone: true, templateUrl: './attendance-report-pagination.html', styleUrl: './attendance-report-pagination.scss' })
export class AttendanceReportPagination {
  @Input() page = 1; @Input() pageSize = 6; @Input() total = 0; @Output() pageChange = new EventEmitter<number>();
  get totalPages(): number { return Math.max(1, Math.ceil(this.total / this.pageSize)); }
  get lastShown(): number { return Math.min(this.page * this.pageSize, this.total); }
  get pages(): number[] { return Array.from({ length: this.totalPages }, (_, i) => i + 1).filter(p => p === 1 || p === this.totalPages || Math.abs(p - this.page) <= 1); }
  setPage(page: number): void { if (page >= 1 && page <= this.totalPages) this.pageChange.emit(page); }
}
