import { useEffect, useMemo, useState } from 'react';
import './dashboard.css';

interface DashboardProps {
  token?: string | null;
  user?: {
    username?: string;
    displayName?: string;
    role?: string;
  } | null;
}

interface Summary {
  totalRevenue: number;
  todayRevenue: number;
  totalOrders: number;
  todayOrders: number;
  activeItems: number;
  activeCustomers: number;
  pendingInvoices: number;
}

interface MonthlyRevenuePoint {
  month: string;
  revenue: number;
}

interface TopItem {
  menuItemId: number;
  item: string;
  category?: string | null;
  unitsSold: number;
  revenue: number;
}

interface RecentOrder {
  id: number;
  orderNumber: string;
  tableNumber?: string | null;
  customerName?: string | null;
  items: number;
  totalAmount: number;
  status: 'NEW' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  createdAt: string;
}

const API_BASE = window.location.hostname === 'localhost'
  ? 'http://localhost:8081'
  : 'https://restro-restaurent-management.onrender.com';
const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
});

async function fetchJson<T>(path: string, token?: string | null): Promise<T> {
  const headers: HeadersInit = {};
  if (token) {
    headers.Authorization = token;
  }

  const response = await fetch(`${API_BASE}${path}`, { headers });
  if (!response.ok) {
    throw new Error(await response.text() || response.statusText);
  }
  return (await response.json()) as T;
}

function statusClass(status: RecentOrder['status']): string {
  return status.toLowerCase().replace('_', '-');
}

export function Dashboard(props: DashboardProps = {}) {
  const [summary, setSummary] = useState<Summary | null>(null);
  const [monthlyRevenue, setMonthlyRevenue] = useState<MonthlyRevenuePoint[]>([]);
  const [topItems, setTopItems] = useState<TopItem[]>([]);
  const [recentOrders, setRecentOrders] = useState<RecentOrder[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      setLoading(true);
      setError('');

      try {
        if (!props.token) {
          throw new Error('Login through the shell to load live dashboard data.');
        }

        const [summaryResponse, monthlyResponse, topResponse, recentResponse] = await Promise.all([
          fetchJson<Summary>('/api/reports/summary', props.token),
          fetchJson<MonthlyRevenuePoint[]>('/api/reports/monthly-revenue', props.token),
          fetchJson<TopItem[]>('/api/reports/top-items', props.token),
          fetchJson<RecentOrder[]>('/api/reports/recent-orders', props.token),
        ]);

        if (cancelled) {
          return;
        }

        setSummary(summaryResponse);
        setMonthlyRevenue(monthlyResponse);
        setTopItems(topResponse);
        setRecentOrders(recentResponse);
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : String(err));
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void load();

    return () => {
      cancelled = true;
    };
  }, [props.token]);

  const stats = useMemo(() => {
    if (!summary) {
      return [];
    }

    return [
      { label: 'Total revenue', value: currency.format(summary.totalRevenue), hint: 'All paid invoices' },
      { label: 'Today revenue', value: currency.format(summary.todayRevenue), hint: 'Paid today' },
      { label: 'Total orders', value: String(summary.totalOrders), hint: 'All orders saved' },
      { label: 'Today orders', value: String(summary.todayOrders), hint: 'Placed today' },
      { label: 'Active items', value: String(summary.activeItems), hint: 'Menu inventory' },
      { label: 'Pending invoices', value: String(summary.pendingInvoices), hint: 'Awaiting payment' },
    ];
  }, [summary]);

  const maxRevenue = Math.max(
    1,
    ...monthlyRevenue.map((entry) => Number.isFinite(entry.revenue) ? entry.revenue : 0),
  );
  const displayName = props.user?.displayName || props.user?.username || 'team';

  return (
    <div className="dashboard">
      <div className="page-head">
        <h2>Dashboard</h2>
        <p>Live summary for {displayName}.</p>
      </div>

      {loading && <div className="state state-loading">Loading live data...</div>}
      {error && <div className="state state-error">{error}</div>}

      {summary && (
        <div className="stats-row">
          {stats.map((stat) => (
            <div className="stat-card ticket-edge" key={stat.label}>
              <span className="stat-label">{stat.label}</span>
              <span className="stat-value">{stat.value}</span>
              <span className="stat-hint">{stat.hint}</span>
            </div>
          ))}
        </div>
      )}

      <div className="charts-row">
        <div className="panel">
          <div className="panel-head">
            <h3>Revenue by month</h3>
            <span className="panel-sub">in INR</span>
          </div>
          <div className="bar-chart">
            {monthlyRevenue.map((point) => (
              <div className="bar-col" key={point.month}>
                <div className="bar" style={{ height: `${(point.revenue / maxRevenue) * 100}%` }}>
                  <span className="bar-value">{currency.format(point.revenue)}</span>
                </div>
                <span className="bar-label">{point.month}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-head">
            <h3>Top items</h3>
          </div>
          <table className="top-items-table">
            <thead>
              <tr>
                <th>Item</th>
                <th>Units</th>
                <th>Revenue</th>
              </tr>
            </thead>
            <tbody>
              {topItems.map((item) => (
                <tr key={item.menuItemId}>
                  <td>
                    <strong>{item.item}</strong>
                    <div className="table-muted">{item.category || '-'}</div>
                  </td>
                  <td>{item.unitsSold}</td>
                  <td className="mono">{currency.format(item.revenue)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="bottom-row">
        <div className="panel">
          <div className="panel-head">
            <h3>Recent orders</h3>
          </div>
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order</th>
                <th>Customer</th>
                <th>Table</th>
                <th>Total</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.map((order) => (
                <tr key={order.id}>
                  <td className="mono">{order.orderNumber}</td>
                  <td>
                    <strong>{order.customerName || 'Walk-in'}</strong>
                    <div className="table-muted">{order.items} item(s)</div>
                  </td>
                  <td>{order.tableNumber || '-'}</td>
                  <td className="mono">{currency.format(order.totalAmount)}</td>
                  <td>
                    <span className={`status-pill ${statusClass(order.status)}`}>{order.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="panel notifications-panel">
          <div className="panel-head">
            <h3>Summary</h3>
          </div>
          <ul className="notif-list">
            <li>
              <span className="notif-dot"></span>
              <div>
                <p className="notif-message">{summary ? summary.activeCustomers : 0} customers in the database</p>
                <span className="notif-time">Source: report-service</span>
              </div>
            </li>
            <li>
              <span className="notif-dot"></span>
              <div>
                <p className="notif-message">{summary ? summary.activeItems : 0} items currently active</p>
                <span className="notif-time">Source: inventory</span>
              </div>
            </li>
            <li>
              <span className="notif-dot"></span>
              <div>
                <p className="notif-message">{summary ? summary.pendingInvoices : 0} invoices pending</p>
                <span className="notif-time">Source: billing</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
