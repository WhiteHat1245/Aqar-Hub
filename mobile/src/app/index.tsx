import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  FlatList,
  Image,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
  TextInput,
  RefreshControl,
} from 'react-native';
import { useRouter } from 'expo-router';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import LoginScreen from '../components/LoginScreen';

interface Apartment {
  id: number;
  title: string;
  type: string;
  region: string;
  basePrice: number;
  roomsCount: number;
  capacity: number;
  images?: string[];
}

export default function HomeScreen() {
  const { token, isLoading } = useAuth();
  const router = useRouter();
  const [apartments, setApartments] = useState<Apartment[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [search, setSearch] = useState('');

  const fetchApartments = useCallback(async () => {
    try {
      const response = await api.get('/apartments');
      setApartments(response.data);
    } catch (error) {
      console.error('Error fetching apartments:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    if (token) {
      fetchApartments();
    }
  }, [token, fetchApartments]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchApartments();
  };

  if (isLoading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#3498db" />
      </View>
    );
  }

  // If not authenticated, render LoginScreen
  if (!token) {
    return <LoginScreen />;
  }

  // Filter apartments locally based on search query
  const filteredApartments = apartments.filter(
    (apt) =>
      apt.title.toLowerCase().includes(search.toLowerCase()) ||
      apt.region.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <SafeAreaView style={styles.container}>
      {/* Header section */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>عقار هب 🏢</Text>
        <Text style={styles.headerSubtitle}>ابحث عن شقتك المثالية للحجز</Text>
      </View>

      {/* Search Input */}
      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="ابحث بالاسم أو المنطقة..."
          value={search}
          onChangeText={setSearch}
          placeholderTextColor="#999"
        />
      </View>

      {loading ? (
        <View style={styles.centered}>
          <ActivityIndicator size="large" color="#3498db" />
        </View>
      ) : (
        <FlatList
          data={filteredApartments}
          keyExtractor={(item) => item.id.toString()}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#3498db']} />
          }
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Text style={styles.emptyText}>لا توجد شقق متاحة حالياً تطابق بحثك</Text>
            </View>
          }
          contentContainerStyle={styles.listContent}
          renderItem={({ item }) => {
            const hasImages = item.images && item.images.length > 0;
            const imageUrl = hasImages
              ? `${api.defaults.baseURL}${item.images![0]}`
              : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80';

            return (
              <TouchableOpacity
                style={styles.card}
                activeOpacity={0.9}
                onPress={() => router.push({ pathname: '/details', params: { id: item.id } })}
              >
                <Image source={{ uri: imageUrl }} style={styles.cardImage} />
                <View style={styles.priceBadge}>
                  <Text style={styles.priceText}>{item.basePrice} د.ل / ليلة</Text>
                </View>

                <View style={styles.cardInfo}>
                  <Text style={styles.cardTitle}>{item.title}</Text>
                  
                  <View style={styles.metaRow}>
                    <Text style={styles.metaText}>📍 {item.region}</Text>
                    <Text style={styles.metaText}>🏠 {item.type}</Text>
                    <Text style={styles.metaText}>🛏️ {item.roomsCount} غرف</Text>
                  </View>
                </View>
              </TouchableOpacity>
            );
          }}
        />
      )}
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
    paddingBottom: 10,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#f1f2f6',
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#7f8c8d',
    textAlign: 'right',
    marginTop: 4,
  },
  searchContainer: {
    padding: 15,
    backgroundColor: '#fff',
  },
  searchInput: {
    backgroundColor: '#f1f2f6',
    borderRadius: 10,
    paddingHorizontal: 15,
    paddingVertical: 10,
    fontSize: 16,
    color: '#2c3e50',
    textAlign: 'right',
  },
  listContent: {
    padding: 15,
    paddingBottom: 80,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 15,
    marginBottom: 20,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.08,
    shadowRadius: 10,
    elevation: 3,
    position: 'relative',
  },
  cardImage: {
    width: '100%',
    height: 180,
    backgroundColor: '#ecf0f1',
  },
  priceBadge: {
    position: 'absolute',
    top: 15,
    right: 15,
    backgroundColor: 'rgba(46, 204, 113, 0.9)',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 6,
  },
  priceText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 14,
  },
  cardInfo: {
    padding: 15,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
    marginBottom: 8,
  },
  metaRow: {
    flexDirection: 'row-reverse',
    justifyContent: 'space-between',
    marginTop: 8,
  },
  metaText: {
    fontSize: 13,
    color: '#7f8c8d',
  },
  emptyContainer: {
    alignItems: 'center',
    paddingVertical: 50,
  },
  emptyText: {
    color: '#7f8c8d',
    fontSize: 16,
  },
});
