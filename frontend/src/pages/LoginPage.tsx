import React, { useState } from 'react';
import { Button, Card, Container, Spinner } from 'react-bootstrap';
import { useAuth } from '../auth/AuthContext';

export default function LoginPage() {
  const { initiateLogin } = useAuth();
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    setLoading(true);
    await initiateLogin();
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
      <Card style={{ width: '360px' }} className="shadow">
        <Card.Body className="p-4 text-center">
          <h4 className="fw-bold mb-1">AuthenID</h4>
          <p className="text-muted mb-4">Admin Panel</p>
          <Button
            variant="primary"
            size="lg"
            className="w-100"
            onClick={handleLogin}
            disabled={loading}
          >
            {loading ? (
              <>
                <Spinner size="sm" className="me-2" />
                Redirecting...
              </>
            ) : (
              'Sign in with AuthenID'
            )}
          </Button>
          <p className="text-muted mt-3 small">
            You will be redirected to the AuthenID authorization server.
          </p>
        </Card.Body>
      </Card>
    </Container>
  );
}
