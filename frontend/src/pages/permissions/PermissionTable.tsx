import React from 'react';
import { Button, Table } from 'react-bootstrap';
import { PermissionResponse } from '../../types/permission';

interface Props {
  permissions: PermissionResponse[];
  onDelete: (p: PermissionResponse) => void;
}

export default function PermissionTable({ permissions, onDelete }: Props) {
  if (permissions.length === 0) {
    return <p className="text-muted">No permissions found.</p>;
  }
  return (
    <Table striped hover responsive>
      <thead className="table-dark">
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Description</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {permissions.map((p) => (
          <tr key={p.id}>
            <td>{p.id}</td>
            <td><code>{p.name}</code></td>
            <td>{p.description ?? <span className="text-muted">—</span>}</td>
            <td className="text-end">
              <Button size="sm" variant="outline-danger" onClick={() => onDelete(p)}>
                Delete
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}
