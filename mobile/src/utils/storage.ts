import { Platform } from 'react-native';
import * as SecureStore from 'expo-secure-store';

export const storage = {
  getItemAsync: async (key: string): Promise<string | null> => {
    if (Platform.OS === 'web') {
      try {
        return localStorage.getItem(key);
      } catch (e) {
        console.error('Error reading from localStorage', e);
        return null;
      }
    }
    try {
      const isSecureAvailable = await SecureStore.isAvailableAsync();
      if (isSecureAvailable) {
        return await SecureStore.getItemAsync(key);
      }
    } catch (e) {
      console.warn('SecureStore not available, falling back to localStorage/memory', e);
    }
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  },

  setItemAsync: async (key: string, value: string): Promise<void> => {
    if (Platform.OS === 'web') {
      try {
        localStorage.setItem(key, value);
      } catch (e) {
        console.error('Error writing to localStorage', e);
      }
      return;
    }
    try {
      const isSecureAvailable = await SecureStore.isAvailableAsync();
      if (isSecureAvailable) {
        await SecureStore.setItemAsync(key, value);
        return;
      }
    } catch (e) {
      console.warn('SecureStore not available, falling back to localStorage/memory', e);
    }
    try {
      localStorage.setItem(key, value);
    } catch {}
  },

  deleteItemAsync: async (key: string): Promise<void> => {
    if (Platform.OS === 'web') {
      try {
        localStorage.removeItem(key);
      } catch (e) {
        console.error('Error removing from localStorage', e);
      }
      return;
    }
    try {
      const isSecureAvailable = await SecureStore.isAvailableAsync();
      if (isSecureAvailable) {
        await SecureStore.deleteItemAsync(key);
        return;
      }
    } catch (e) {
      console.warn('SecureStore not available, falling back to localStorage/memory', e);
    }
    try {
      localStorage.removeItem(key);
    } catch {}
  },
};
