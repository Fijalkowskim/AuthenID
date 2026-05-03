import React from 'react';
import { Badge, Button, Table } from 'react-bootstrap';
import { RoleResponse } from '../../types/role';

interface Props {
  roles: RoleResponse[];
  onDelete: (r: RoleResponse) => void;
}

export default function RoleTable({ roles, onDelete }: Props) {
  if (roles.length === 0) return <p className="text-muted">No roles found.</p>;
  return (
    <Table striped hover responsive>
      <thead className="table-dark">
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Description</th>
          <th>Permissions</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {roles.map((r) => (
          <tr key={r.id}>
            <td>{r.id}</td>
            <td><strong>{r.name}</strong></td>
            <td>{r.description ?? <span className="text-muted">—</span>}</td>
            <td>
              {r.permissions.length === 0
                ? <span className="text-muted">none</span>
                : r.permissions.map((p) => (
                    <Badge bg="secondary" className="me-1" key={p}>{p}</Badge>
                  ))}
            </td>
            <td className="text-end">
              <Button size="sm" variant="outline-danger" onClick={() => onDelete(r)}>Delete</Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}
