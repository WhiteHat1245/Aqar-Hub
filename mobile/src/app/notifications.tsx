import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
  RefreshControl,
  Alert,
} from 'react-native';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

export default function NotificationsScreen() {
  const { token } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchNotifications = () => {
    api.get('/notifications')
      .then((response) => {
        setNotifications(response.data);
      })
      .catch((error) => {
        console.error('Error fetching notifications:', error);
      })
      .finally(() => {
        setLoading(false);
        setRefreshing(false);
      });
  };

  useEffect(() => {
    if (token) {
      fetchNotifications();
    }
  }, [token]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchNotifications();
  };

  const markAsRead = async (id: number, isRead: boolean) => {
    if (isRead) return;

    try {
      await api.patch(`/notifications/${id}/read`);
      // Update state locally
      setNotifications(prev =>
        prev.map(notif => (notif.id === id ? { ...notif, isRead: true } : notif))
      );
    } catch (error) {
      console.error('Error marking notification as read:', error);
      Alert.alert('خطأ', 'فشل تحديث حالة الإشعار');
    }
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'booking_confirmed':
        return '🎉';
      case 'booking_cancelled':
        return '🚫';
      case 'loyalty_points_earned':
        return '✨';
      default:
        return '🔔';
    }
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#3498db" />
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>مركز الإشعارات 🔔</Text>
      </View>

      <FlatList
        data={notifications}
        keyExtractor={(item) => item.id.toString()}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#3498db']} />
        }
        contentContainerStyle={styles.listContent}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>صندوق الوارد فارغ. لا توجد تنبيهات جديدة.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <TouchableOpacity
            style={[styles.card, !item.isRead && styles.unreadCard]}
            onPress={() => markAsRead(item.id, item.isRead)}
            activeOpacity={0.8}
          >
            <View style={styles.cardHeader}>
              <Text style={styles.icon}>{getIcon(item.type)}</Text>
              <View style={styles.titleContainer}>
                <Text style={[styles.title, !item.isRead && styles.unreadText]}>{item.title}</Text>
                <Text style={styles.date}>
                  {new Date(item.createdAt).toLocaleDateString('ar-EG', {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </Text>
              </View>
            </View>
            <Text style={styles.message}>{item.message}</Text>
            {!item.isRead && <View style={styles.unreadDot} />}
          </TouchableOpacity>
        )}
      />
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
  },
  header: {
    paddingHorizontal: 20,
    paddingTop: 15,
    paddingBottom: 15,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#f1f2f6',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
  },
  listContent: {
    padding: 15,
    paddingBottom: 80,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    position: 'relative',
  },
  unreadCard: {
    backgroundColor: '#f7fafc',
    borderColor: '#cbd5e0',
  },
  cardHeader: {
    flexDirection: 'row-reverse',
    alignItems: 'center',
    marginBottom: 8,
  },
  icon: {
    fontSize: 24,
    marginLeft: 12,
  },
  titleContainer: {
    flex: 1,
    alignItems: 'flex-end',
  },
  title: {
    fontSize: 15,
    color: '#4a5568',
    fontWeight: '500',
    textAlign: 'right',
  },
  unreadText: {
    fontWeight: 'bold',
    color: '#2d3748',
  },
  date: {
    fontSize: 11,
    color: '#a0aec0',
    marginTop: 2,
  },
  message: {
    fontSize: 14,
    color: '#718096',
    textAlign: 'right',
    lineHeight: 20,
  },
  unreadDot: {
    position: 'absolute',
    top: 15,
    left: 15,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#e53e3e',
  },
  emptyContainer: {
    alignItems: 'center',
    paddingVertical: 50,
  },
  emptyText: {
    color: '#7f8c8d',
    fontSize: 15,
  },
});
