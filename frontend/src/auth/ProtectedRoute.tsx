import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './AuthContext';
import LoadingSpinner from '../components/common/LoadingSpinner';

export default function ProtectedRoute() {
  const { token, isLoading } = useAuth();

  if (isLoading) return <LoadingSpinner />;
  if (!token) return <Navigate to="/login" replace />;

  return <Outlet />;
}
