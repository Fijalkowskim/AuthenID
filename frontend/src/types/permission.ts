export interface PermissionResponse {
  id: number;
  name: string;
  description: string | null;
}

export interface PermissionCreateRequest {
  name: string;
  description?: string;
}
