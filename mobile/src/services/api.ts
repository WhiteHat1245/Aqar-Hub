import axios from 'axios';
import { storage } from '../utils/storage';

const API_URL = 'http://localhost:3000'; // استبدل localhost بـ IP جهازك الخاص (مثال: 192.168.1.100) عند الفحص على هاتف حقيقي

export const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor لحقن التوكن تلقائياً قبل خروج أي طلب لـ APIs المحمية
api.interceptors.request.use(
  async (config) => {
    const token = await storage.getItemAsync('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
