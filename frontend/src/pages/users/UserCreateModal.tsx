import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { userApi } from '../../api/userApi';
import { roleApi } from '../../api/roleApi';
import { UserCreateRequest } from '../../types/user';
import MultiSelectField from '../../components/common/MultiSelectField';

interface Props {
  show: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function UserCreateModal({ show, onClose, onSuccess }: Props) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [allRoles, setAllRoles] = useState<string[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (show) {
      roleApi.getAll().then((r) => setAllRoles(r.map((x) => x.name)));
    }
  }, [show]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const data: UserCreateRequest = {
        username: username.trim(),
        email: email.trim(),
        password,
        phoneNumber: phone.trim() || undefined,
        roleNames: selectedRoles,
      };
      await userApi.create(data);
      toast.success(`User "${username}" has been created.`);
      setUsername(''); setEmail(''); setPassword(''); setPhone(''); setSelectedRoles([]);
      onSuccess(); onClose();
    } catch {
      toast.error('Failed to create user.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>New user</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Username *</Form.Label>
            <Form.Control value={username} onChange={(e) => setUsername(e.target.value)} required />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Email *</Form.Label>
            <Form.Control type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Password *</Form.Label>
            <Form.Control type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={8} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Phone</Form.Label>
            <Form.Control value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="optional" />
          </Form.Group>
          <MultiSelectField label="Roles" options={allRoles} selected={selectedRoles} onChange={setSelectedRoles} />
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
