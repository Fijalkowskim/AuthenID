export type UserStatus =
  | 'ACTIVE'
  | 'LOCKED'
  | 'SUSPENDED'
  | 'PENDING_VERIFICATION'
  | 'DELETED';

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  phoneNumber: string | null;
  emailVerified: boolean;
  status: UserStatus;
  createdAt: string;
  updatedAt: string;
  lastLoginAt: string | null;
  roles: string[];
}

export interface UserCreateRequest {
  username: string;
  password: string;
  email: string;
  phoneNumber?: string;
  roleNames?: string[];
}

export interface UserUpdateRequest {
  username: string;
  email: string;
  phoneNumber?: string;
  emailVerified: boolean;
  status: UserStatus;
  roleNames: string[];
}
