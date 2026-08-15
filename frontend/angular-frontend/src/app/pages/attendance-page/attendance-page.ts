import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { AttendanceRecord, AttendanceStatus } from '../../core/models';

interface AttendanceRow extends AttendanceRecord { status: AttendanceStatus; note: string; }

@Component({ selector: 'app-attendance-page', standalone: true, imports: [CommonModule, FormsModule, RouterLink], templateUrl: './attendance-page.html', styleUrl: './attendance-page.scss' })
export class AttendancePage implements OnInit {
  month = this.monthKey(new Date()); selectedDate = this.dateKey(new Date()); staff: AttendanceRecord[] = []; records: AttendanceRecord[] = []; rows: AttendanceRow[] = [];
  loading = false; saving = false; error = ''; success = '';
  readonly statuses: Array<{ value: AttendanceStatus; label: string; code: string }> = [
    { value: 'PRESENT', label: 'Present', code: 'P' }, { value: 'ABSENT', label: 'Absent', code: 'A' }, { value: 'LATE', label: 'Late', code: 'L' }, { value: 'HOLIDAY', label: 'Holiday', code: 'H' }, { value: 'WEEK_OFF', label: 'Week off', code: 'WO' }, { value: 'LEAVE_WITHOUT_PAY', label: 'LWP', code: 'LWP' },
  ];
  constructor(private api: ApiService, private auth: AuthService) {}
  get isManager(): boolean { return this.auth.currentUser()?.role === 'MANAGER'; }
  get isOwner(): boolean { return this.auth.currentUser()?.role === 'OWNER'; }
  get monthLabel(): string { return new Date(`${this.month}-01T00:00:00`).toLocaleDateString(undefined, { month: 'long', year: 'numeric' }); }
  get days(): Array<Date | null> { const start = new Date(`${this.month}-01T00:00:00`); const first = start.getDay(); const count = new Date(start.getFullYear(), start.getMonth() + 1, 0).getDate(); return [...Array(first).fill(null), ...Array.from({ length: count }, (_, index) => new Date(start.getFullYear(), start.getMonth(), index + 1))]; }
  get selectedLabel(): string { return new Date(`${this.selectedDate}T00:00:00`).toLocaleDateString(undefined, { weekday: 'long', day: 'numeric', month: 'long' }); }
  get presentCount(): number { return this.rows.filter(row => row.status === 'PRESENT').length; }
  get editable(): boolean { return !this.isManager || this.selectedDate === this.today; }
  get today(): string { return this.dateKey(new Date()); }
  ngOnInit(): void { void this.load(); }
  async load(): Promise<void> { this.loading = true; this.error = ''; try { const [staff, report] = await Promise.all([this.api.get<AttendanceRecord[]>('/api/attendance/staff'), this.api.get<{ records: AttendanceRecord[] }>(`/api/attendance/report?month=${this.month}`)]); this.staff = staff; this.records = report.records; this.setRows(); } catch (error) { this.error = this.message(error); } finally { this.loading = false; } }
  async changeMonth(month: string): Promise<void> { this.month = month; if (!this.selectedDate.startsWith(month)) this.selectedDate = `${month}-01`; await this.load(); }
  selectDate(day: Date): void { const date = this.dateKey(day); if (!this.canSelect(date)) return; this.selectedDate = date; this.success = ''; this.setRows(); }
  canSelect(date: string): boolean { return date <= this.today && (!this.isManager || date === this.today); }
  canManageDay(day: Date): boolean { return this.canSelect(this.dateKey(day)); }
  isSelected(day: Date): boolean { return this.selectedDate === this.dateKey(day); }
  statusForDate(day: Date): string { const date = this.dateKey(day); const statuses = this.records.filter(record => record.attendanceDate === date).map(record => record.status); if (!statuses.length) return '—'; if (statuses.every(status => status === 'PRESENT')) return 'P'; if (statuses.some(status => status === 'HOLIDAY')) return 'H'; if (statuses.some(status => status === 'WEEK_OFF')) return 'WO'; return `${statuses.filter(status => status === 'PRESENT').length}/${statuses.length}`; }
  setStatus(row: AttendanceRow, status: AttendanceStatus): void { if (this.editable) row.status = status; }
  async save(): Promise<void> { if (!this.editable || !this.rows.length) return; this.saving = true; this.error = ''; try { await this.api.post('/api/attendance', { attendanceDate: this.selectedDate, records: this.rows.map(row => ({ staffId: row.staffId, status: row.status, note: row.note || null })) }); this.success = `Attendance saved for ${this.selectedLabel}.`; await this.load(); } catch (error) { this.error = this.message(error); } finally { this.saving = false; } }
  private setRows(): void { const records = new Map(this.records.filter(record => record.attendanceDate === this.selectedDate).map(record => [record.staffId, record])); this.rows = this.staff.map(staff => { const record = records.get(staff.staffId); return { ...(record ?? staff), status: record?.status ?? 'PRESENT', note: record?.note ?? '' } as AttendanceRow; }); }
  dateKey(date: Date): string { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`; }
  private monthKey(date: Date): string { return this.dateKey(date).slice(0, 7); }
  private message(error: unknown): string { return error instanceof Error ? error.message : String(error); }
}
