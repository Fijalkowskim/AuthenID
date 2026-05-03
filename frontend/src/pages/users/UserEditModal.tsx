import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { userApi } from '../../api/userApi';
import { roleApi } from '../../api/roleApi';
import { UserResponse, UserStatus, UserUpdateRequest } from '../../types/user';
import MultiSelectField from '../../components/common/MultiSelectField';

const STATUSES: UserStatus[] = ['ACTIVE', 'LOCKED', 'SUSPENDED', 'PENDING_VERIFICATION', 'DELETED'];

interface Props {
  user: UserResponse | null;
  onClose: () => void;
  onSuccess: () => void;
}

export default function UserEditModal({ user, onClose, onSuccess }: Props) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [emailVerified, setEmailVerified] = useState(false);
  const [status, setStatus] = useState<UserStatus>('ACTIVE');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [allRoles, setAllRoles] = useState<string[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (user) {
      setUsername(user.username);
      setEmail(user.email);
      setPhone(user.phoneNumber ?? '');
      setEmailVerified(user.emailVerified);
      setStatus(user.status);
      setSelectedRoles(user.roles);
      roleApi.getAll().then((r) => setAllRoles(r.map((x) => x.name)));
    }
  }, [user]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setSaving(true);
    try {
      const data: UserUpdateRequest = {
        username: username.trim(),
        email: email.trim(),
        phoneNumber: phone.trim() || undefined,
        emailVerified,
        status,
        roleNames: selectedRoles,
      };
      await userApi.update(user.id, data);
      toast.success(`User "${username}" has been updated.`);
      onSuccess(); onClose();
    } catch {
      toast.error('Failed to update user.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal show={!!user} onHide={onClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Edit user</Modal.Title>
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
            <Form.Label>Phone</Form.Label>
            <Form.Control value={phone} onChange={(e) => setPhone(e.target.value)} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Status</Form.Label>
            <Form.Select value={status} onChange={(e) => setStatus(e.target.value as UserStatus)}>
              {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </Form.Select>
          </Form.Group>
          <Form.Check
            className="mb-3"
            type="checkbox"
            id="emailVerified"
            label="Email verified"
            checked={emailVerified}
            onChange={(e) => setEmailVerified(e.target.checked)}
          />
          <MultiSelectField label="Roles" options={allRoles} selected={selectedRoles} onChange={setSelectedRoles} />
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" variant="primary" disabled={saving}>
            {saving ? <Spinner size="sm" /> : 'Save'}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
