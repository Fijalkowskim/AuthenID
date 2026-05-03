import api from './axiosInstance';
import { PermissionResponse, PermissionCreateRequest } from '../types/permission';

const BASE = '/api/admin/permissions';

export const permissionApi = {
  getAll: () => api.get<PermissionResponse[]>(BASE).then((r) => r.data),
  getById: (id: number) => api.get<PermissionResponse>(`${BASE}/${id}`).then((r) => r.data),
  create: (data: PermissionCreateRequest) =>
    api.post<PermissionResponse>(BASE, data).then((r) => r.data),
  delete: (id: number) => api.delete(`${BASE}/${id}`),
};
