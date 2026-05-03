import api from './axiosInstance';
import { RoleResponse, RoleCreateRequest } from '../types/role';

const BASE = '/api/admin/roles';

export const roleApi = {
  getAll: () => api.get<RoleResponse[]>(BASE).then((r) => r.data),
  getById: (id: number) => api.get<RoleResponse>(`${BASE}/${id}`).then((r) => r.data),
  create: (data: RoleCreateRequest) => api.post<RoleResponse>(BASE, data).then((r) => r.data),
  delete: (id: number) => api.delete(`${BASE}/${id}`),
};
