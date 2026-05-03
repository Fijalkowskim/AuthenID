import React, { useCallback, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { roleApi } from '../../api/roleApi';
import { RoleResponse } from '../../types/role';
import PageHeader from '../../components/common/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmModal from '../../components/common/ConfirmModal';
import RoleTable from './RoleTable';
import RoleCreateModal from './RoleCreateModal';

export default function RolesPage() {
  const [roles, setRoles] = useState<RoleResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [toDelete, setToDelete] = useState<RoleResponse | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setRoles(await roleApi.getAll());
    } catch {
      toast.error('Failed to load roles.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async () => {
    if (!toDelete) return;
    try {
      await roleApi.delete(toDelete.id);
      toast.success(`Role "${toDelete.name}" has been deleted.`);
      setToDelete(null);
      load();
    } catch {
      toast.error('Failed to delete role.');
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <>
      <PageHeader title="Roles" onAdd={() => setShowCreate(true)} addLabel="New role" />
      <RoleTable roles={roles} onDelete={setToDelete} />

      <RoleCreateModal show={showCreate} onClose={() => setShowCreate(false)} onSuccess={load} />
      <ConfirmModal
        show={!!toDelete}
        title="Delete role"
        message={`Are you sure you want to delete role "${toDelete?.name}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  );
}
