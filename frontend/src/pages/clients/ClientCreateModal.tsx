import React, { useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { clientApi } from '../../api/clientApi';
import { OAuthClientCreateRequest, OAuthClientSecretResponse } from '../../types/client';

const DEFAULT_SCOPES = ['openid', 'profile', 'email'];

interface Props {
  show: boolean;
  onClose: () => void;
  onSuccess: () => void;
  onSecret: (s: OAuthClientSecretResponse) => void;
}

export default function ClientCreateModal({ show, onClose, onSuccess, onSecret }: Props) {
  const [clientId, setClientId] = useState('');
  const [clientName, setClientName] = useState('');
  const [redirectUri, setRedirectUri] = useState('');
  const [selectedScopes, setSelectedScopes] = useState<string[]>(['openid']);
  const [saving, setSaving] = useState(false);

  const toggleScope = (scope: string) => {
    setSelectedScopes((prev) =>
      prev.includes(scope) ? prev.filter((s) => s !== scope) : [...prev, scope]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const data: OAuthClientCreateRequest = {
        clientId: clientId.trim(),
        clientName: clientName.trim(),
        redirectUris: redirectUri.split('\n').map((s) => s.trim()).filter(Boolean),
        scopes: selectedScopes,
      };
      const result = await clientApi.create(data);
      toast.success(`Client "${clientName}" has been created.`);
      setClientId(''); setClientName(''); setRedirectUri(''); setSelectedScopes(['openid']);
      onSuccess();
      onClose();
      onSecret(result);
    } catch {
      toast.error('Failed to create client.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>New OAuth Client</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Client ID *</Form.Label>
            <Form.Control value={clientId} onChange={(e) => setClientId(e.target.value)} placeholder="my-app" required />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Name *</Form.Label>
            <Form.Control value={clientName} onChange={(e) => setClientName(e.target.value)} placeholder="My Application" required />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Redirect URI (one per line) *</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              value={redirectUri}
              onChange={(e) => setRedirectUri(e.target.value)}
              placeholder="https://myapp.com/callback"
              required
            />
          </Form.Group>
          <Form.Group>
            <Form.Label>Scopes</Form.Label>
            {DEFAULT_SCOPES.map((s) => (
              <Form.Check
                key={s}
                type="checkbox"
                id={`scope-${s}`}
                label={s}
                checked={selectedScopes.includes(s)}
                onChange={() => toggleScope(s)}
              />
            ))}
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" variant="primary" disabled={saving}>
            {saving ? <Spinner size="sm" /> : 'Create'}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
