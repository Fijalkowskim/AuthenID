import React, { useCallback, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { permissionApi } from '../../api/permissionApi';
import { PermissionResponse } from '../../types/permission';
import PageHeader from '../../components/common/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmModal from '../../components/common/ConfirmModal';
import PermissionTable from './PermissionTable';
import PermissionCreateModal from './PermissionCreateModal';

export default function PermissionsPage() {
  const [permissions, setPermissions] = useState<PermissionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [toDelete, setToDelete] = useState<PermissionResponse | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setPermissions(await permissionApi.getAll());
    } catch {
      toast.error('Failed to load permissions.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async () => {
    if (!toDelete) return;
    try {
      await permissionApi.delete(toDelete.id);
      toast.success(`Permission "${toDelete.name}" has been deleted.`);
      setToDelete(null);
      load();
    } catch {
      toast.error('Failed to delete permission.');
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <>
      <PageHeader title="Permissions" onAdd={() => setShowCreate(true)} addLabel="New permission" />
      <PermissionTable permissions={permissions} onDelete={setToDelete} />

      <PermissionCreateModal
        show={showCreate}
        onClose={() => setShowCreate(false)}
        onSuccess={load}
      />
      <ConfirmModal
        show={!!toDelete}
        title="Delete permission"
        message={`Are you sure you want to delete permission "${toDelete?.name}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  );
}
