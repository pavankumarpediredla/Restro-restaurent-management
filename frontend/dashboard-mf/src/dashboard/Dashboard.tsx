import './dashboard.css';

interface StatCard {
  label: string;
  value: string;
  delta: string;
  positive: boolean;
}

interface RecentOrder {
  id: string;
  table: string;
  items: number;
  total: string;
  status: 'New' | 'In Kitchen' | 'Ready' | 'Billed';
}

interface NotificationItem {
  message: string;
  time: string;
}

// TEMP mock data — swap for report-service / order-service calls
// (GET /api/reports/summary, GET /api/orders?limit=5) once the gateway is live.
const stats: StatCard[] = [
  { label: "Today's Sales", value: '₹48,260', delta: '+12.4%', positive: true },
  { label: 'Orders Today', value: '186', delta: '+6.1%', positive: true },
  { label: 'Avg. Ticket', value: '₹259', delta: '-2.3%', positive: false },
  { label: 'Tables Occupied', value: '14 / 22', delta: '64%', positive: true },
];

const salesByDay = [
  { day: 'Mon', value: 32 },
  { day: 'Tue', value: 41 },
  { day: 'Wed', value: 38 },
  { day: 'Thu', value: 52 },
  { day: 'Fri', value: 68 },
  { day: 'Sat', value: 91 },
  { day: 'Sun', value: 74 },
];

const revenueSplit = [
  { label: 'Dine-in', value: 58, color: 'var(--ember)' },
  { label: 'Takeaway', value: 27, color: 'var(--sage)' },
  { label: 'Delivery', value: 15, color: 'var(--amber)' },
];

const recentOrders: RecentOrder[] = [
  { id: '#1042', table: 'T5', items: 4, total: '₹860', status: 'In Kitchen' },
  { id: '#1041', table: 'T2', items: 2, total: '₹410', status: 'Ready' },
  { id: '#1040', table: 'Takeaway', items: 1, total: '₹190', status: 'New' },
  { id: '#1039', table: 'T8', items: 6, total: '₹1,320', status: 'Billed' },
  { id: '#1038', table: 'T3', items: 3, total: '₹540', status: 'Billed' },
];

const notifications: NotificationItem[] = [
  { message: 'Order #1042 sent to kitchen', time: '2 min ago' },
  { message: 'Stock low: Paneer (2kg left)', time: '18 min ago' },
  { message: 'Invoice #889 generated for T3', time: '26 min ago' },
];

function statusClass(status: RecentOrder['status']): string {
  return status.toLowerCase().replace(' ', '-');
}

interface DashboardProps {
  token?: string | null;
  user?: { name: string; role: string } | null;
}

export function Dashboard(_props: DashboardProps = {}) {
  const maxSales = Math.max(...salesByDay.map((d) => d.value));
  const offset1 = revenueSplit[0].value;
  const offset2 = revenueSplit[0].value + revenueSplit[1].value;

  return (
    <div className="dashboard">
      <div className="page-head">
        <h2>Dashboard</h2>
        <p>Here's how the floor is doing today.</p>
      </div>

      {/* quick access stat cards */}
      <div className="stats-row">
        {stats.map((stat) => (
          <div className="stat-card ticket-edge" key={stat.label}>
            <span className="stat-label">{stat.label}</span>
            <span className="stat-value">{stat.value}</span>
            <span className={'stat-delta ' + (stat.positive ? 'positive' : 'negative')}>
              {stat.delta}
            </span>
          </div>
        ))}
      </div>

      <div className="charts-row">
        {/* sales bar chart */}
        <div className="panel">
          <div className="panel-head">
            <h3>Sales this week</h3>
            <span className="panel-sub">in ₹’000</span>
          </div>
          <div className="bar-chart">
            {salesByDay.map((d) => (
              <div className="bar-col" key={d.day}>
                <div className="bar" style={{ height: `${(d.value / maxSales) * 100}%` }}>
                  <span className="bar-value">{d.value}</span>
                </div>
                <span className="bar-label">{d.day}</span>
              </div>
            ))}
          </div>
        </div>

        {/* revenue split donut */}
        <div className="panel">
          <div className="panel-head">
            <h3>Revenue by channel</h3>
          </div>
          <div className="donut-wrap">
            <svg viewBox="0 0 42 42" className="donut">
              <circle cx="21" cy="21" r="15.9" fill="transparent" stroke="var(--paper-dim)" strokeWidth="6" />
              <circle
                cx="21" cy="21" r="15.9" fill="transparent"
                stroke={revenueSplit[0].color} strokeWidth="6"
                strokeDasharray={`${revenueSplit[0].value} ${100 - revenueSplit[0].value}`}
                strokeDashoffset={25}
              />
              <circle
                cx="21" cy="21" r="15.9" fill="transparent"
                stroke={revenueSplit[1].color} strokeWidth="6"
                strokeDasharray={`${revenueSplit[1].value} ${100 - revenueSplit[1].value}`}
                strokeDashoffset={25 - offset1}
              />
              <circle
                cx="21" cy="21" r="15.9" fill="transparent"
                stroke={revenueSplit[2].color} strokeWidth="6"
                strokeDasharray={`${revenueSplit[2].value} ${100 - revenueSplit[2].value}`}
                strokeDashoffset={25 - offset2}
              />
            </svg>
            <div className="donut-legend">
              {revenueSplit.map((r) => (
                <div className="legend-item" key={r.label}>
                  <span className="dot" style={{ background: r.color }}></span>
                  {r.label} <strong>{r.value}%</strong>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="bottom-row">
        {/* recent orders */}
        <div className="panel">
          <div className="panel-head">
            <h3>Recent orders</h3>
          </div>
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order</th>
                <th>Table</th>
                <th>Items</th>
                <th>Total</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.map((o) => (
                <tr key={o.id}>
                  <td className="mono">{o.id}</td>
                  <td>{o.table}</td>
                  <td>{o.items}</td>
                  <td className="mono">{o.total}</td>
                  <td>
                    <span className={'status-pill ' + statusClass(o.status)}>{o.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* notifications */}
        <div className="panel notifications-panel">
          <div className="panel-head">
            <h3>Notifications</h3>
          </div>
          <ul className="notif-list">
            {notifications.map((n) => (
              <li key={n.message}>
                <span className="notif-dot"></span>
                <div>
                  <p className="notif-message">{n.message}</p>
                  <span className="notif-time">{n.time}</span>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
