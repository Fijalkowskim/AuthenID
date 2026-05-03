import React, { useCallback, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { clientApi } from '../../api/clientApi';
import { OAuthClientResponse, OAuthClientSecretResponse } from '../../types/client';
import PageHeader from '../../components/common/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmModal from '../../components/common/ConfirmModal';
import ClientTable from './ClientTable';
import ClientCreateModal from './ClientCreateModal';
import ClientSecretModal from './ClientSecretModal';

export default function ClientsPage() {
  const [clients, setClients] = useState<OAuthClientResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [revealedSecret, setRevealedSecret] = useState<OAuthClientSecretResponse | null>(null);
  const [rotateTarget, setRotateTarget] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setClients(await clientApi.getAll());
    } catch {
      toast.error('Failed to load OAuth clients.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleRotate = async () => {
    if (!rotateTarget) return;
    try {
      const result = await clientApi.rotateSecret(rotateTarget);
      toast.success(`Secret for client "${rotateTarget}" has been rotated.`);
      setRotateTarget(null);
      setRevealedSecret(result);
    } catch {
      toast.error('Failed to rotate secret.');
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <>
      <PageHeader title="OAuth Clients" onAdd={() => setShowCreate(true)} addLabel="New client" />
      <ClientTable clients={clients} onRotate={setRotateTarget} />

      <ClientCreateModal
        show={showCreate}
        onClose={() => setShowCreate(false)}
        onSuccess={load}
        onSecret={setRevealedSecret}
      />
      <ClientSecretModal
        secret={revealedSecret}
        onClose={() => setRevealedSecret(null)}
      />
      <ConfirmModal
        show={!!rotateTarget}
        title="Rotate secret"
        message={`Are you sure you want to rotate the secret for client "${rotateTarget}"? The current secret will be invalidated immediately.`}
        confirmLabel="Rotate"
        variant="warning"
        onConfirm={handleRotate}
        onCancel={() => setRotateTarget(null)}
      />
    </>
  );
}
