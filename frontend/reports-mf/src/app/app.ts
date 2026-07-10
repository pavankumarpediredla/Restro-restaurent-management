// Standalone dev harness — lets you run `ng serve` and see Reports on its
// own at http://localhost:4205, without the Shell.
// The Shell itself never imports this file — it only imports mount.ts.
import { Component } from '@angular/core';
import { Reports } from './reports/reports';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [Reports],
  template: `<app-reports />`,
})
export class App {}
