export interface OAuthClientResponse {
  clientId: string;
  clientName: string;
  redirectUris: string[];
  scopes: string[];
  requireProofKey: boolean;
}

export interface OAuthClientCreateRequest {
  clientId: string;
  clientName: string;
  redirectUris: string[];
  scopes: string[];
}

export interface OAuthClientSecretResponse {
  clientId: string;
  clientSecret: string;
  clientName: string;
  redirectUris: string[];
  scopes: string[];
}
