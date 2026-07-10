// Standalone dev harness — lets you run `npm run dev` and see the Dashboard
// on its own at http://localhost:5173, without the Shell.
// The Shell itself never imports this file — it only imports `mount.tsx`.
import { Dashboard } from './dashboard/Dashboard';

export default function App() {
  return <Dashboard />;
}
