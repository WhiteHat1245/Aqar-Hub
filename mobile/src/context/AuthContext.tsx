import React, { createContext, useState, useEffect, useContext } from 'react';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

interface AuthContextType {
  token: string | null;
  isLoading: boolean;
  login: (token: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  isLoading: true,
  login: async () => {},
  logout: async () => {},
});

const isWeb = Platform.OS === 'web';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadToken() {
      try {
        const storedToken = isWeb
          ? localStorage.getItem('access_token')
          : await SecureStore.getItemAsync('access_token');
        setToken(storedToken);
      } catch (e) {
        console.error('Error loading token:', e);
      } finally {
        setIsLoading(false);
      }
    }
    loadToken();
  }, []);

  const login = async (newToken: string) => {
    if (isWeb) {
      localStorage.setItem('access_token', newToken);
    } else {
      await SecureStore.setItemAsync('access_token', newToken);
    }
    setToken(newToken);
  };

  const logout = async () => {
    if (isWeb) {
      localStorage.removeItem('access_token');
    } else {
      await SecureStore.deleteItemAsync('access_token');
    }
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
