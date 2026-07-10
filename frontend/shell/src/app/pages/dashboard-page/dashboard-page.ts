import { Component } from '@angular/core';
import { RemoteMount } from '../../remote-mount/remote-mount';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [RemoteMount],
  template: `<app-remote-mount remoteKey="dashboard" />`,
})
export class DashboardPage {}
