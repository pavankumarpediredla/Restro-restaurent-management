export type Role = 'ADMIN' | 'MANAGER' | 'KITCHEN';

export interface AppUser {
  id: number;
  username: string;
  displayName: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
}

export interface PricePoint {
  cycle: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  amount: number;
}

export interface MenuItem {
  id: number;
  name: string;
  description?: string | null;
  category?: string | null;
  active: boolean;
  prices: PricePoint[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderLine {
  menuItemId: number;
  menuItemName: string;
  priceCycle: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface OrderLineCreate {
  menuItemId: number;
  quantity: number;
  priceCycle: 'DAILY' | 'WEEKLY' | 'MONTHLY';
}

export interface OrderCreateRequest {
  customerId?: number | null;
  customerName?: string | null;
  customerPhone?: string | null;
  customerEmail?: string | null;
  customerAddress?: string | null;
  tableNumber?: string | null;
  notes?: string | null;
  items: OrderLineCreate[];
}

export interface Order {
  id: number;
  orderNumber: string;
  status: 'NEW' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  customerId?: number | null;
  customerName?: string | null;
  customerPhone?: string | null;
  tableNumber?: string | null;
  notes?: string | null;
  totalAmount: number;
  createdAt: string;
  acceptedAt?: string | null;
  createdBy?: string | null;
  acceptedBy?: string | null;
  items: OrderLine[];
}

export interface Customer {
  id: number;
  fullName: string;
  phone?: string | null;
  email?: string | null;
  address?: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  orderId: number;
  orderNumber: string;
  customerId?: number | null;
  customerName?: string | null;
  status: 'DRAFT' | 'ISSUED' | 'PAID' | 'CANCELLED';
  subtotal: number;
  taxAmount: number;
  discountAmount: number;
  totalAmount: number;
  createdAt: string;
  paidAt?: string | null;
  notes?: string | null;
}

export interface InvoiceCreateRequest {
  orderId: number;
  taxAmount?: number | null;
  discountAmount?: number | null;
  notes?: string | null;
}

export interface DashboardSummary {
  totalRevenue: number;
  todayRevenue: number;
  totalOrders: number;
  todayOrders: number;
  activeItems: number;
  activeCustomers: number;
  pendingInvoices: number;
}

export interface MonthlyRevenuePoint {
  month: string;
  revenue: number;
}

export interface TopItem {
  menuItemId: number;
  item: string;
  category?: string | null;
  unitsSold: number;
  revenue: number;
}

export interface RecentOrder {
  id: number;
  orderNumber: string;
  tableNumber?: string | null;
  customerName?: string | null;
  items: number;
  totalAmount: number;
  status: 'NEW' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  createdAt: string;
}
