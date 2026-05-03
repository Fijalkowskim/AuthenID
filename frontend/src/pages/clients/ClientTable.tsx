import React from 'react';
import { Badge, Button, Table } from 'react-bootstrap';
import { OAuthClientResponse } from '../../types/client';

interface Props {
  clients: OAuthClientResponse[];
  onRotate: (clientId: string) => void;
}

export default function ClientTable({ clients, onRotate }: Props) {
  if (clients.length === 0) return <p className="text-muted">No OAuth clients found.</p>;
  return (
    <Table striped hover responsive>
      <thead className="table-dark">
        <tr>
          <th>Client ID</th>
          <th>Name</th>
          <th>Redirect URIs</th>
          <th>Scopes</th>
          <th>PKCE</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {clients.map((c) => (
          <tr key={c.clientId}>
            <td><code>{c.clientId}</code></td>
            <td>{c.clientName}</td>
            <td>
              {c.redirectUris.map((u) => (
                <div key={u} className="small text-muted">{u}</div>
              ))}
            </td>
            <td>
              {c.scopes.map((s) => <Badge bg="info" className="me-1" key={s}>{s}</Badge>)}
            </td>
            <td>
              {c.requireProofKey
                ? <Badge bg="success">Required</Badge>
                : <Badge bg="secondary">No</Badge>}
            </td>
            <td className="text-end">
              <Button size="sm" variant="outline-warning" onClick={() => onRotate(c.clientId)}>
                Rotate secret
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}
