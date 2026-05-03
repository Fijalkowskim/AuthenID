import React, { useEffect, useState } from 'react';
import { Col, Row, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { userApi } from '../api/userApi';
import { roleApi } from '../api/roleApi';
import { permissionApi } from '../api/permissionApi';
import { clientApi } from '../api/clientApi';

interface Stats {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  roles: number;
  permissions: number;
  clients: number;
}

interface StatCardProps {
  label: string;
  value: number | undefined;
  to: string;
}

function StatCard({ label, value, to }: StatCardProps) {
  return (
    <Col>
      <Link to={to} style={{ textDecoration: 'none' }}>
        <div
          style={{
            background: '#fff',
            border: '1px solid #e5e7eb',
            borderRadius: '8px',
            padding: '24px 28px',
            transition: 'box-shadow 0.15s',
          }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLDivElement).style.boxShadow = '0 4px 16px rgba(0,0,0,0.08)')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLDivElement).style.boxShadow = 'none')}
        >
          <div style={{ fontSize: '13px', color: '#6b7280', fontWeight: 500, marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.05em', whiteSpace: 'nowrap' }}>
            {label}
          </div>
          <div style={{ fontSize: '36px', fontWeight: 700, color: '#111827', lineHeight: 1 }}>
            {value ?? '—'}
          </div>
        </div>
      </Link>
    </Col>
  );
}

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      userApi.getAll(),
      roleApi.getAll(),
      permissionApi.getAll(),
      clientApi.getAll(),
    ]).then(([users, roles, permissions, clients]) => {
      const userList = users as { status: string }[];
      const activeUsers = userList.filter((u) => u.status === 'ACTIVE').length;
      setStats({
        totalUsers: userList.length,
        activeUsers,
        inactiveUsers: userList.length - activeUsers,
        roles: roles.length,
        permissions: permissions.length,
        clients: clients.length,
      });
    }).finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="d-flex justify-content-center mt-5">
        <Spinner animation="border" style={{ color: '#374151' }} />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1100px' }}>
      <div style={{ marginBottom: '32px' }}>
        <h4 style={{ fontWeight: 700, color: '#111827', marginBottom: '4px' }}>Overview</h4>
        <span style={{ color: '#6b7280', fontSize: '14px' }}>System summary and recent activity</span>
      </div>

      <Row xs={2} md={3} lg={6} className="g-3">
        <StatCard label="Total Users"    value={stats?.totalUsers}    to="/users" />
        <StatCard label="Active Users"   value={stats?.activeUsers}   to="/users" />
        <StatCard label="Inactive Users" value={stats?.inactiveUsers} to="/users" />
        <StatCard label="Roles"          value={stats?.roles}         to="/roles" />
        <StatCard label="Permissions"    value={stats?.permissions}   to="/permissions" />
        <StatCard label="OAuth Clients"  value={stats?.clients}       to="/clients" />
      </Row>
    </div>
  );
}
