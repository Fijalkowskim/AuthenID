import React, { useRef, useState } from 'react';
import { Alert, Button, Form, InputGroup, Modal } from 'react-bootstrap';
import { OAuthClientSecretResponse } from '../../types/client';

interface Props {
  secret: OAuthClientSecretResponse | null;
  onClose: () => void;
}

export default function ClientSecretModal({ secret, onClose }: Props) {
  const [copied, setCopied] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleCopy = () => {
    if (secret) {
      navigator.clipboard.writeText(secret.clientSecret);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <Modal show={!!secret} onHide={onClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>Client secret — {secret?.clientName}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Alert variant="warning">
          <strong>Warning!</strong> The secret is displayed only once. Copy it and store it in a safe place — after closing this window it cannot be retrieved.
        </Alert>
        <Form.Label>Client ID</Form.Label>
        <Form.Control readOnly value={secret?.clientId ?? ''} className="mb-3 font-monospace" />
        <Form.Label>Client Secret</Form.Label>
        <InputGroup>
          <Form.Control
            ref={inputRef}
            readOnly
            value={secret?.clientSecret ?? ''}
            className="font-monospace"
          />
          <Button variant={copied ? 'success' : 'outline-secondary'} onClick={handleCopy}>
            {copied ? 'Copied!' : 'Copy'}
          </Button>
        </InputGroup>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={onClose}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
}
