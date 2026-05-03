import React from 'react';
import { NavLink } from 'react-router-dom';
import { Nav } from 'react-bootstrap';

const links = [
  { to: '/',            label: 'Dashboard' },
  { to: '/users',       label: 'Users' },
  { to: '/roles',       label: 'Roles' },
  { to: '/permissions', label: 'Permissions' },
  { to: '/clients',     label: 'OAuth Clients' },
];

export default function Sidebar() {
  return (
    <div
      style={{
        minHeight: '100vh',
        width: '220px',
        flexShrink: 0,
        background: '#111827',
        display: 'flex',
        flexDirection: 'column',
        padding: '24px 0',
      }}
    >
      <div style={{ padding: '0 20px 28px' }}>
        <div style={{ fontWeight: 700, color: '#f9fafb', fontSize: '16px', letterSpacing: '-0.01em' }}>
          AuthenID
        </div>
        <div style={{ color: '#6b7280', fontSize: '12px', marginTop: '2px' }}>Admin Panel</div>
      </div>
      <Nav className="flex-column" style={{ gap: '2px', padding: '0 10px' }}>
        {links.map(({ to, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            style={({ isActive }) => ({
              display: 'block',
              padding: '9px 14px',
              borderRadius: '6px',
              fontSize: '14px',
              fontWeight: 500,
              textDecoration: 'none',
              color: isActive ? '#f9fafb' : '#9ca3af',
              background: isActive ? '#1f2937' : 'transparent',
              transition: 'background 0.12s, color 0.12s',
            })}
            onMouseEnter={(e) => {
              const el = e.currentTarget;
              if (!el.classList.contains('active')) {
                el.style.color = '#e5e7eb';
                el.style.background = '#1a2333';
              }
            }}
            onMouseLeave={(e) => {
              const el = e.currentTarget;
              if (!el.classList.contains('active')) {
                el.style.color = '#9ca3af';
                el.style.background = 'transparent';
              }
            }}
          >
            {label}
          </NavLink>
        ))}
      </Nav>
    </div>
  );
}
