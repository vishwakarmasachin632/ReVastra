import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authApi } from '../api/services';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('revastra_token') || '');
  const [profile, setProfile] = useState(() => {
    const raw = localStorage.getItem('revastra_profile');
    return raw ? JSON.parse(raw) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) localStorage.setItem('revastra_token', token);
    else localStorage.removeItem('revastra_token');
  }, [token]);

  useEffect(() => {
    if (profile) localStorage.setItem('revastra_profile', JSON.stringify(profile));
    else localStorage.removeItem('revastra_profile');
  }, [profile]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const auth = await authApi.login({ email, password });
      setToken(auth.token);
      const me = await authApi.getProfile(auth.token);
      setProfile(me);
      return me;
    } finally {
      setLoading(false);
    }
  };

  const register = async (payload) => authApi.register(payload);

  const refreshProfile = async () => {
    if (!token) return null;
    const me = await authApi.getProfile(token);
    setProfile(me);
    return me;
  };

  const logout = () => {
    setToken('');
    setProfile(null);
  };

  const value = useMemo(
    () => ({ token, profile, loading, login, register, refreshProfile, logout, isAuthenticated: !!token }),
    [token, profile, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
