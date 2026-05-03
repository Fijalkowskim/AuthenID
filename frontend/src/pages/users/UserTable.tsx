import React from 'react';
import { Badge, Button, Table } from 'react-bootstrap';
import { UserResponse } from '../../types/user';
import UserStatusBadge from './UserStatusBadge';

interface Props {
  users: UserResponse[];
  onEdit: (u: UserResponse) => void;
  onDelete: (u: UserResponse) => void;
}

export default function UserTable({ users, onEdit, onDelete }: Props) {
  if (users.length === 0) return <p className="text-muted">No users found.</p>;
  return (
    <Table striped hover responsive>
      <thead className="table-dark">
        <tr>
          <th>#</th>
          <th>Username</th>
          <th>Email</th>
          <th>Status</th>
          <th>Roles</th>
          <th>Last login</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {users.map((u) => (
          <tr key={u.id}>
            <td>{u.id}</td>
            <td><strong>{u.username}</strong></td>
            <td>{u.email}</td>
            <td><UserStatusBadge status={u.status} /></td>
            <td>
              {u.roles.length === 0
                ? <span className="text-muted">none</span>
                : u.roles.map((r) => <Badge bg="primary" className="me-1" key={r}>{r}</Badge>)}
            </td>
            <td>
              {u.lastLoginAt
                ? new Date(u.lastLoginAt).toLocaleString('en-GB')
                : <span className="text-muted">—</span>}
            </td>
            <td className="text-end">
              <Button size="sm" variant="outline-primary" className="me-1" onClick={() => onEdit(u)}>Edit</Button>
              <Button size="sm" variant="outline-danger" onClick={() => onDelete(u)}>Delete</Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}
