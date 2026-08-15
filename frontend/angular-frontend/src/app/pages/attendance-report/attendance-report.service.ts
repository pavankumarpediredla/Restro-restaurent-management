import { Injectable } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { AttendanceRecord } from '../../core/models';
import { AttendanceReportData, AttendanceReportEmployee, ReportStatusCode } from './attendance-report.types';

@Injectable({ providedIn: 'root' })
export class AttendanceReportService {
  constructor(private api: ApiService) {}

  async load(month: string): Promise<AttendanceReportData> {
    const [staff, report] = await Promise.all([
      this.api.get<AttendanceRecord[]>('/api/attendance/staff'),
      this.api.get<{ records: AttendanceRecord[] }>(`/api/attendance/report?month=${month}`),
    ]);
    const records = report.records;
    const byStaff = new Map<number, AttendanceReportEmployee>();
    staff.forEach(member => byStaff.set(member.staffId, {
      staffId: member.staffId,
      name: member.staffName || member.staffUsername,
      code: member.staffUsername,
      department: 'Not assigned',
      employeeStatus: member.staffEnabled === false ? 'Ex-Employee' : 'Active',
      attendance: new Map(),
    }));
    records.forEach(record => {
      const employee = byStaff.get(record.staffId);
      if (employee && record.attendanceDate && record.status) {
        employee.attendance.set(record.attendanceDate, this.toReportStatus(record.status));
      }
    });
    return { employees: [...byStaff.values()], records };
  }

  private toReportStatus(status: NonNullable<AttendanceRecord['status']>): ReportStatusCode {
    return ({ PRESENT: 'P', ABSENT: 'A', LATE: 'L', HOLIDAY: 'H', WEEK_OFF: 'WO', LEAVE: 'LWP', LEAVE_WITHOUT_PAY: 'LWP', NOT_AVAILABLE: 'NA' } as const)[status];
  }
}
