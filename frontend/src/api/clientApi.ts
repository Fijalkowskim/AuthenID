import api from './axiosInstance';
import {
  OAuthClientResponse,
  OAuthClientCreateRequest,
  OAuthClientSecretResponse,
} from '../types/client';

const BASE = '/api/admin/clients';

export const clientApi = {
  getAll: () => api.get<OAuthClientResponse[]>(BASE).then((r) => r.data),
  getById: (clientId: string) =>
    api.get<OAuthClientResponse>(`${BASE}/${clientId}`).then((r) => r.data),
  create: (data: OAuthClientCreateRequest) =>
    api.post<OAuthClientSecretResponse>(BASE, data).then((r) => r.data),
  rotateSecret: (clientId: string) =>
    api.post<OAuthClientSecretResponse>(`${BASE}/${clientId}/rotate-secret`).then((r) => r.data),
};
