import { Component } from '@angular/core';
import { RemoteMount } from '../../remote-mount/remote-mount';

@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [RemoteMount],
  template: `<app-remote-mount remoteKey="reports" />`,
})
export class ReportsPage {}
