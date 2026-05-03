import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { Container, Alert } from 'react-bootstrap';

export default function CallbackPage() {
  const [searchParams] = useSearchParams();
  const { handleCallback } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const called = useRef(false);

  useEffect(() => {
    if (called.current) return;
    called.current = true;

    const code = searchParams.get('code');
    const errorParam = searchParams.get('error');

    if (errorParam) {
      setError(`Authorization denied: ${errorParam}`);
      return;
    }

    if (!code) {
      setError('Authorization code not found in callback URL.');
      return;
    }

    handleCallback(code)
      .then(() => navigate('/', { replace: true }))
      .catch((err: Error) => setError(err.message));
  }, [searchParams, handleCallback, navigate]);

  if (error) {
    return (
      <Container className="mt-5">
        <Alert variant="danger">
          <Alert.Heading>Login error</Alert.Heading>
          <p>{error}</p>
        </Alert>
      </Container>
    );
  }

  return <LoadingSpinner />;
}
