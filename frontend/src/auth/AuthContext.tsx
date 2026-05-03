import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { buildAuthUrl, exchangeCodeForToken } from './pkce';

const TOKEN_KEY = 'authenid_token';
const VERIFIER_KEY = 'authenid_pkce_verifier';

interface AuthContextValue {
  token: string | null;
  isLoading: boolean;
  initiateLogin: () => Promise<void>;
  handleCallback: (code: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem(TOKEN_KEY);
    setToken(stored);
    setIsLoading(false);
  }, []);

  const initiateLogin = useCallback(async () => {
    const { url, verifier } = await buildAuthUrl();
    localStorage.setItem(VERIFIER_KEY, verifier);
    window.location.href = url;
  }, []);

  const handleCallback = useCallback(async (code: string) => {
    const verifier = localStorage.getItem(VERIFIER_KEY);
    if (!verifier) {
      throw new Error('PKCE verifier not found — please try logging in again.');
    }
    localStorage.removeItem(VERIFIER_KEY);

    const accessToken = await exchangeCodeForToken(code, verifier);
    localStorage.setItem(TOKEN_KEY, accessToken);
    setToken(accessToken);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
  }, []);

  const value = useMemo(
    () => ({ token, isLoading, initiateLogin, handleCallback, logout }),
    [token, isLoading, initiateLogin, handleCallback, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}
