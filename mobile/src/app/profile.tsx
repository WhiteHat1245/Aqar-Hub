import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
  ScrollView,
  RefreshControl,
  Alert,
} from 'react-native';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

interface UserProfile {
  id: number;
  name: string;
  email: string;
  phoneNumber?: string;
  role: string;
  loyaltyPoints: number;
}

export default function ProfileScreen() {
  const { token, logout } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Simple JWT decoder using global atob
  const getUserIdFromToken = (jwtToken: string): number | null => {
    try {
      const payloadBase64 = jwtToken.split('.')[1];
      const decodedPayload = atob(payloadBase64);
      const parsed = JSON.parse(decodedPayload);
      return parsed.sub; // Sub contains the user id in NestJS JWT Strategy
    } catch (e) {
      console.error('Error decoding JWT token:', e);
      return null;
    }
  };

  const fetchProfile = () => {
    if (!token) return;
    const userId = getUserIdFromToken(token);
    if (!userId) {
      setLoading(false);
      setRefreshing(false);
      return;
    }

    api.get(`/users/${userId}`)
      .then((response) => {
        setProfile(response.data);
      })
      .catch((error) => {
        console.error('Error fetching profile:', error);
        Alert.alert('خطأ', 'فشل تحميل بيانات الملف الشخصي');
      })
      .finally(() => {
        setLoading(false);
        setRefreshing(false);
      });
  };

  useEffect(() => {
    fetchProfile();
  }, [token]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchProfile();
  };

  const handleLogout = () => {
    Alert.alert(
      'تسجيل الخروج',
      'هل أنت متأكد من رغبتك في تسجيل الخروج؟',
      [
        { text: 'إلغاء', style: 'cancel' },
        { text: 'تسجيل خروج', onPress: () => logout(), style: 'destructive' },
      ]
    );
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#3498db" />
      </View>
    );
  }

  if (!profile) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>فشل تحميل ملف المستخدم</Text>
        <TouchableOpacity style={styles.logoutButton} onPress={logout}>
          <Text style={styles.logoutText}>تسجيل الخروج</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#3498db']} />
        }
      >
        <View style={styles.avatarCard}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{profile.name.charAt(0).toUpperCase()}</Text>
          </View>
          <Text style={styles.userName}>{profile.name}</Text>
          <Text style={styles.userRoleBadge}>{profile.role.toUpperCase()}</Text>
        </View>

        {/* Loyalty points card */}
        <View style={styles.loyaltyCard}>
          <Text style={styles.loyaltyTitle}>نقاط الولاء الحالية ✨</Text>
          <Text style={styles.loyaltyPoints}>{profile.loyaltyPoints} نقطة</Text>
          <Text style={styles.loyaltySubtitle}>
            استمر في الحجز لكسب المزيد من النقاط والحصول على عروض حصرية!
          </Text>
        </View>

        {/* User details card */}
        <View style={styles.infoCard}>
          <Text style={styles.sectionTitle}>معلومات الحساب</Text>

          <View style={styles.infoRow}>
            <Text style={styles.infoValue}>{profile.email}</Text>
            <Text style={styles.infoLabel}>البريد الإلكتروني</Text>
          </View>

          <View style={styles.infoRow}>
            <Text style={styles.infoValue}>{profile.phoneNumber || 'غير محدد'}</Text>
            <Text style={styles.infoLabel}>رقم الهاتف</Text>
          </View>
        </View>

        <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
          <Text style={styles.logoutText}>تسجيل الخروج</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f6fa',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: {
    fontSize: 16,
    color: '#e74c3c',
    marginBottom: 20,
  },
  scrollContent: {
    padding: 20,
    paddingBottom: 80,
  },
  avatarCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.05,
    shadowRadius: 10,
    elevation: 2,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#3498db',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  avatarText: {
    color: '#fff',
    fontSize: 32,
    fontWeight: 'bold',
  },
  userName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  userRoleBadge: {
    fontSize: 11,
    fontWeight: 'bold',
    color: '#3498db',
    backgroundColor: '#ebf5fb',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
    marginTop: 8,
  },
  loyaltyCard: {
    backgroundColor: '#2c3e50',
    borderRadius: 16,
    padding: 20,
    alignItems: 'center',
    marginBottom: 20,
    shadowColor: '#2c3e50',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.15,
    shadowRadius: 10,
    elevation: 3,
  },
  loyaltyTitle: {
    fontSize: 14,
    color: '#bdc3c7',
    fontWeight: '600',
  },
  loyaltyPoints: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#f1c40f',
    marginVertical: 10,
  },
  loyaltySubtitle: {
    fontSize: 12,
    color: '#ecf0f1',
    textAlign: 'center',
    lineHeight: 18,
  },
  infoCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 20,
    marginBottom: 25,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.05,
    shadowRadius: 10,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
    marginBottom: 16,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f1f2f6',
  },
  infoLabel: {
    fontSize: 14,
    color: '#7f8c8d',
  },
  infoValue: {
    fontSize: 14,
    color: '#2c3e50',
    fontWeight: '500',
  },
  logoutButton: {
    backgroundColor: '#fff',
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#e74c3c',
  },
  logoutText: {
    color: '#e74c3c',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
