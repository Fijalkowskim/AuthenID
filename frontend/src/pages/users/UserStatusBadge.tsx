import React from 'react';
import { Badge } from 'react-bootstrap';
import { UserStatus } from '../../types/user';

const CONFIG: Record<UserStatus, { label: string; bg: string }> = {
  ACTIVE: { label: 'Active', bg: 'success' },
  LOCKED: { label: 'Locked', bg: 'danger' },
  SUSPENDED: { label: 'Suspended', bg: 'warning' },
  PENDING_VERIFICATION: { label: 'Pending', bg: 'info' },
  DELETED: { label: 'Deleted', bg: 'secondary' },
};

export default function UserStatusBadge({ status }: { status: UserStatus }) {
  const { label, bg } = CONFIG[status] ?? { label: status, bg: 'secondary' };
  return <Badge bg={bg}>{label}</Badge>;
}
