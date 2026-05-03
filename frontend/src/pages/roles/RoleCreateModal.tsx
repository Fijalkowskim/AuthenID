import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { roleApi } from '../../api/roleApi';
import { permissionApi } from '../../api/permissionApi';
import { RoleCreateRequest } from '../../types/role';
import MultiSelectField from '../../components/common/MultiSelectField';

interface Props {
  show: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function RoleCreateModal({ show, onClose, onSuccess }: Props) {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedPerms, setSelectedPerms] = useState<string[]>([]);
  const [allPerms, setAllPerms] = useState<string[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (show) {
      permissionApi.getAll().then((p) => setAllPerms(p.map((x) => x.name)));
    }
  }, [show]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setSaving(true);
    try {
      const data: RoleCreateRequest = {
        name: name.trim(),
        description: description.trim() || undefined,
        permissionNames: selectedPerms,
      };
      await roleApi.create(data);
      toast.success(`Role "${name}" has been created.`);
      setName(''); setDescription(''); setSelectedPerms([]);
      onSuccess(); onClose();
    } catch {
      toast.error('Failed to create role.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>New role</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Name *</Form.Label>
            <Form.Control value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. MANAGER" required />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Description</Form.Label>
            <Form.Control value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Role description (optional)" />
          </Form.Group>
          <MultiSelectField
            label="Permissions"
            options={allPerms}
            selected={selectedPerms}
            onChange={setSelectedPerms}
          />
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
