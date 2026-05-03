import React, { useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { permissionApi } from '../../api/permissionApi';
import { PermissionCreateRequest } from '../../types/permission';

interface Props {
  show: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function PermissionCreateModal({ show, onClose, onSuccess }: Props) {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setSaving(true);
    try {
      const data: PermissionCreateRequest = { name: name.trim(), description: description.trim() || undefined };
      await permissionApi.create(data);
      toast.success(`Permission "${name}" has been created.`);
      setName('');
      setDescription('');
      onSuccess();
      onClose();
    } catch {
      toast.error('Failed to create permission.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>New permission</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Name *</Form.Label>
            <Form.Control
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. USER_READ"
              required
            />
          </Form.Group>
          <Form.Group>
            <Form.Label>Description</Form.Label>
            <Form.Control
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Permission description (optional)"
            />
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
