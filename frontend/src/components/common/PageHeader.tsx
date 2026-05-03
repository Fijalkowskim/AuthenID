import React from 'react';
import { Button, Stack } from 'react-bootstrap';

interface Props {
  title: string;
  onAdd?: () => void;
  addLabel?: string;
}

export default function PageHeader({ title, onAdd, addLabel = 'Add' }: Props) {
  return (
    <Stack direction="horizontal" className="mb-4">
      <h2 className="mb-0">{title}</h2>
      {onAdd && (
        <Button variant="primary" className="ms-auto" onClick={onAdd}>
          + {addLabel}
        </Button>
      )}
    </Stack>
  );
}
