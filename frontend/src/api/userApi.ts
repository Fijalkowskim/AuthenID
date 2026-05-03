import api from './axiosInstance';
import { UserResponse, UserCreateRequest, UserUpdateRequest } from '../types/user';

const BASE = '/api/admin/users';

export const userApi = {
  getAll: () => api.get<UserResponse[]>(BASE).then((r) => r.data),
  getById: (id: number) => api.get<UserResponse>(`${BASE}/${id}`).then((r) => r.data),
  create: (data: UserCreateRequest) => api.post<UserResponse>(BASE, data).then((r) => r.data),
  update: (id: number, data: UserUpdateRequest) =>
    api.put<UserResponse>(`${BASE}/${id}`, data).then((r) => r.data),
  delete: (id: number) => api.delete(`${BASE}/${id}`),
};
