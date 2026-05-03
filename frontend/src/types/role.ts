export interface RoleResponse {
  id: number;
  name: string;
  description: string | null;
  permissions: string[];
}

export interface RoleCreateRequest {
  name: string;
  description?: string;
  permissionNames?: string[];
}
