import { AttendanceRecord, AttendanceStatus } from '../../core/models';

export type ReportStatusCode = 'P' | 'A' | 'L' | 'H' | 'WO' | 'LWP' | 'NA';

export interface AttendanceStatusDefinition {
  code: ReportStatusCode;
  label: string;
  apiStatus?: AttendanceStatus;
  className: string;
}

export const attendanceStatusConfig: Record<ReportStatusCode, AttendanceStatusDefinition> = {
  P: { code: 'P', label: 'Present', apiStatus: 'PRESENT', className: 'attendance-present' },
  A: { code: 'A', label: 'Absent', apiStatus: 'ABSENT', className: 'attendance-absent' },
  L: { code: 'L', label: 'Late', apiStatus: 'LATE', className: 'attendance-late' },
  H: { code: 'H', label: 'Holiday', apiStatus: 'HOLIDAY', className: 'attendance-holiday' },
  WO: { code: 'WO', label: 'Week Off', apiStatus: 'WEEK_OFF', className: 'attendance-week-off' },
  LWP: { code: 'LWP', label: 'Leave Without Pay', apiStatus: 'LEAVE_WITHOUT_PAY', className: 'attendance-lwp' },
  NA: { code: 'NA', label: 'Not Available', apiStatus: 'NOT_AVAILABLE', className: 'attendance-na' },
};

export interface AttendanceReportFilters {
  employee: string;
  employeeStatus: 'ALL' | 'ACTIVE' | 'INACTIVE';
  attendanceStatus: 'ALL' | ReportStatusCode;
}

export interface AttendanceReportEmployee {
  staffId: number;
  name: string;
  code: string;
  department: string;
  employeeStatus: 'Active' | 'Ex-Employee';
  attendance: Map<string, ReportStatusCode>;
}

export interface AttendanceReportData {
  employees: AttendanceReportEmployee[];
  records: AttendanceRecord[];
}
