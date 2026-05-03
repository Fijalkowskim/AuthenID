import React, { useCallback, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { userApi } from '../../api/userApi';
import { UserResponse } from '../../types/user';
import PageHeader from '../../components/common/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmModal from '../../components/common/ConfirmModal';
import UserTable from './UserTable';
import UserCreateModal from './UserCreateModal';
import UserEditModal from './UserEditModal';

export default function UsersPage() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [toEdit, setToEdit] = useState<UserResponse | null>(null);
  const [toDelete, setToDelete] = useState<UserResponse | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setUsers(await userApi.getAll());
    } catch {
      toast.error('Failed to load users.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async () => {
    if (!toDelete) return;
    try {
      await userApi.delete(toDelete.id);
      toast.success(`User "${toDelete.username}" has been deleted.`);
      setToDelete(null);
      load();
    } catch {
      toast.error('Failed to delete user.');
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <>
      <PageHeader title="Users" onAdd={() => setShowCreate(true)} addLabel="New user" />
      <UserTable users={users} onEdit={setToEdit} onDelete={setToDelete} />

      <UserCreateModal show={showCreate} onClose={() => setShowCreate(false)} onSuccess={load} />
      <UserEditModal user={toEdit} onClose={() => setToEdit(null)} onSuccess={load} />
      <ConfirmModal
        show={!!toDelete}
        title="Delete user"
        message={`Are you sure you want to delete user "${toDelete?.username}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  );
}
