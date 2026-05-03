import React from 'react';
import { Button, Navbar } from 'react-bootstrap';
import { useAuth } from '../../auth/AuthContext';

export default function TopBar() {
  const { logout } = useAuth();
  return (
    <Navbar className="bg-white border-bottom px-4 py-2">
      <span className="text-muted small">AuthenID Admin</span>
      <Button variant="outline-secondary" size="sm" className="ms-auto" onClick={logout}>
        Logout
      </Button>
    </Navbar>
  );
}
