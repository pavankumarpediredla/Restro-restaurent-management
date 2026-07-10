import { Component } from '@angular/core';
import { RemoteMount } from '../../remote-mount/remote-mount';

@Component({
  selector: 'app-orders-page',
  standalone: true,
  imports: [RemoteMount],
  template: `<app-remote-mount remoteKey="orders" />`,
})
export class OrdersPage {}
