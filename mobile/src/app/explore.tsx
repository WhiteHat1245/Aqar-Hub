import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
  Image,
  ActivityIndicator,
  SafeAreaView,
  ScrollView,
} from 'react-native';
import { useRouter } from 'expo-router';
import { api } from '../services/api';

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

export default function ExploreScreen() {
  const router = useRouter();
  const [apartments, setApartments] = useState<Apartment[]>([]);
  const [loading, setLoading] = useState(false);

  // Filter form states
  const [region, setRegion] = useState('');
  const [type, setType] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [roomsCount, setRoomsCount] = useState('');

  const handleSearch = async () => {
    setLoading(true);
    try {
      const params: any = {};
      if (region) params.region = region;
      if (type) params.type = type;
      if (minPrice) params.minPrice = minPrice;
      if (maxPrice) params.maxPrice = maxPrice;
      if (roomsCount) params.roomsCount = roomsCount;

      const response = await api.get('/apartments', { params });
      setApartments(response.data);
    } catch (error) {
      console.error('Error exploring apartments:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>البحث المتقدم 🔍</Text>
      </View>

      {/* Filter panel */}
      <View style={styles.filterCard}>
        <ScrollView nestedScrollEnabled>
          <View style={styles.row}>
            <View style={styles.inputGroup}>
              <Text style={styles.label}>المنطقة</Text>
              <TextInput
                style={styles.input}
                placeholder="مثال: طرابلس"
                value={region}
                onChangeText={setRegion}
                placeholderTextColor="#999"
              />
            </View>

            <View style={styles.inputGroup}>
              <Text style={styles.label}>نوع الشقة</Text>
              <TextInput
                style={styles.input}
                placeholder="مثال: فاخرة"
                value={type}
                onChangeText={setType}
                placeholderTextColor="#999"
              />
            </View>
          </View>

          <View style={styles.row}>
            <View style={styles.inputGroup}>
              <Text style={styles.label}>أقل سعر</Text>
              <TextInput
                style={styles.input}
                placeholder="0"
                value={minPrice}
                onChangeText={setMinPrice}
                keyboardType="numeric"
                placeholderTextColor="#999"
              />
            </View>

            <View style={styles.inputGroup}>
              <Text style={styles.label}>أعلى سعر</Text>
              <TextInput
                style={styles.input}
                placeholder="1000"
                value={maxPrice}
                onChangeText={setMaxPrice}
                keyboardType="numeric"
                placeholderTextColor="#999"
              />
            </View>
          </View>

          <View style={styles.inputGroupFull}>
            <Text style={styles.label}>عدد الغرف</Text>
            <TextInput
              style={styles.input}
              placeholder="مثال: 3"
              value={roomsCount}
              onChangeText={setRoomsCount}
              keyboardType="numeric"
              placeholderTextColor="#999"
            />
          </View>

          <TouchableOpacity style={styles.searchButton} onPress={handleSearch}>
            <Text style={styles.searchButtonText}>تطبيق الفلاتر والبحث</Text>
          </TouchableOpacity>
        </ScrollView>
      </View>

      {/* Results */}
      {loading ? (
        <View style={styles.centered}>
          <ActivityIndicator size="large" color="#3498db" />
        </View>
      ) : (
        <FlatList
          data={apartments}
          keyExtractor={(item) => item.id.toString()}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Text style={styles.emptyText}>اضبط الفلاتر واضغط بحث لعرض النتائج</Text>
            </View>
          }
          renderItem={({ item }) => {
            const hasImages = item.images && item.images.length > 0;
            const imageUrl = hasImages
              ? `${api.defaults.baseURL}${item.images![0]}`
              : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80';

            return (
              <TouchableOpacity
                style={styles.resultCard}
                onPress={() => router.push({ pathname: '/details', params: { id: item.id } })}
              >
                <Image source={{ uri: imageUrl }} style={styles.resultImage} />
                <View style={styles.resultInfo}>
                  <Text style={styles.resultTitle}>{item.title}</Text>
                  <Text style={styles.resultRegion}>📍 {item.region}</Text>
                  <Text style={styles.resultPrice}>{item.basePrice} د.ل / ليلة</Text>
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
  filterCard: {
    backgroundColor: '#fff',
    margin: 15,
    padding: 15,
    borderRadius: 15,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 5,
    elevation: 2,
  },
  row: {
    flexDirection: 'row-reverse',
    justifyContent: 'space-between',
    gap: 10,
  },
  inputGroup: {
    flex: 1,
    marginBottom: 12,
  },
  inputGroupFull: {
    marginBottom: 15,
  },
  label: {
    fontSize: 13,
    color: '#34495e',
    marginBottom: 6,
    textAlign: 'right',
    fontWeight: '600',
  },
  input: {
    backgroundColor: '#f1f2f6',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    fontSize: 14,
    color: '#2c3e50',
    textAlign: 'right',
  },
  searchButton: {
    backgroundColor: '#3498db',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 5,
  },
  searchButtonText: {
    color: '#fff',
    fontSize: 15,
    fontWeight: 'bold',
  },
  listContent: {
    paddingHorizontal: 15,
    paddingBottom: 80,
  },
  resultCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    marginBottom: 12,
    flexDirection: 'row-reverse',
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#e2e8f0',
  },
  resultImage: {
    width: 100,
    height: 100,
    backgroundColor: '#ecf0f1',
  },
  resultInfo: {
    flex: 1,
    padding: 12,
    justifyContent: 'center',
  },
  resultTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
  },
  resultRegion: {
    fontSize: 13,
    color: '#7f8c8d',
    textAlign: 'right',
    marginVertical: 4,
  },
  resultPrice: {
    fontSize: 14,
    color: '#2ecc71',
    fontWeight: 'bold',
    textAlign: 'right',
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
