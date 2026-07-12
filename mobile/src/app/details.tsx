import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  Image,
  ScrollView,
  TextInput,
  TouchableOpacity,
  Alert,
  SafeAreaView,
} from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { api } from '../services/api';

interface Apartment {
  id: number;
  title: string;
  type: string;
  region: string;
  basePrice: number;
  roomsCount: number;
  capacity: number;
  amenities: { [key: string]: boolean };
  images?: string[];
}

export default function DetailsScreen() {
  const { id } = useLocalSearchParams();
  const router = useRouter();
  const [apartment, setApartment] = useState<Apartment | null>(null);
  const [loading, setLoading] = useState(true);
  const [bookingLoading, setBookingLoading] = useState(false);

  // Prepopulate dates
  const todayStr = new Date().toISOString().split('T')[0];
  const tomorrowStr = new Date(Date.now() + 86400000).toISOString().split('T')[0];

  const [startDate, setStartDate] = useState(todayStr);
  const [endDate, setEndDate] = useState(tomorrowStr);
  const [totalPrice, setTotalPrice] = useState(0);
  const [points, setPoints] = useState(0);

  useEffect(() => {
    if (id) {
      api.get(`/apartments/${id}`)
        .then((response) => {
          setApartment(response.data);
          calculatePrice(response.data.basePrice, startDate, endDate);
        })
        .catch((error) => {
          console.error('Error fetching apartment details:', error);
          Alert.alert('خطأ', 'فشل تحميل تفاصيل الشقة');
        })
        .finally(() => setLoading(false));
    }
  }, [id]);

  const calculatePrice = (basePrice: number, start: string, end: string) => {
    const sDate = new Date(start);
    const eDate = new Date(end);
    if (!isNaN(sDate.getTime()) && !isNaN(eDate.getTime()) && eDate > sDate) {
      const diffTime = eDate.getTime() - sDate.getTime();
      const days = Math.ceil(diffTime / (1000 * 60000 * 60 * 24));
      const total = days * basePrice;
      setTotalPrice(total);
      setPoints(Math.floor(total / 10)); // Loyalty points = 10%
    } else {
      setTotalPrice(0);
      setPoints(0);
    }
  };

  const handleDateChange = (type: 'start' | 'end', val: string) => {
    if (type === 'start') {
      setStartDate(val);
      if (apartment) calculatePrice(apartment.basePrice, val, endDate);
    } else {
      setEndDate(val);
      if (apartment) calculatePrice(apartment.basePrice, startDate, val);
    }
  };

  const handleBooking = async () => {
    if (!apartment) return;

    const sDate = new Date(startDate);
    const eDate = new Date(endDate);
    if (isNaN(sDate.getTime()) || isNaN(eDate.getTime())) {
      Alert.alert('خطأ', 'يرجى إدخال تاريخ حجز صحيح بتنسيق YYYY-MM-DD');
      return;
    }

    setBookingLoading(true);
    try {
      await api.post('/bookings', {
        apartmentId: apartment.id,
        startDate,
        endDate,
      });

      Alert.alert(
        'تهانينا! 🎉',
        `تم تأكيد حجزك بنجاح وكسبت ${points} نقطة ولاء جديدة.`,
        [{ text: 'رائع', onPress: () => router.replace('/') }]
      );
    } catch (error: any) {
      const errorMsg = error.response?.data?.message || 'حدث خطأ أثناء إتمام الحجز';
      Alert.alert('فشل الحجز', Array.isArray(errorMsg) ? errorMsg[0] : errorMsg);
    } finally {
      setBookingLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#3498db" />
      </View>
    );
  }

  if (!apartment) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>الشقة غير موجودة أو تم حذفها</Text>
      </View>
    );
  }

  const hasImages = apartment.images && apartment.images.length > 0;
  const imageUrl = hasImages
    ? `${api.defaults.baseURL}${apartment.images![0]}`
    : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80';

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView contentContainerStyle={styles.container}>
        <Image source={{ uri: imageUrl }} style={styles.image} />

        <View style={styles.infoCard}>
          <Text style={styles.title}>{apartment.title}</Text>
          <Text style={styles.region}>📍 {apartment.region}</Text>
          <Text style={styles.price}>{apartment.basePrice} د.ل / ليلة</Text>

          <View style={styles.divider} />

          <Text style={styles.sectionTitle}>المواصفات الأساسية</Text>
          <View style={styles.specGrid}>
            <View style={styles.specItem}>
              <Text style={styles.specLabel}>النوع</Text>
              <Text style={styles.specValue}>{apartment.type}</Text>
            </View>
            <View style={styles.specItem}>
              <Text style={styles.specLabel}>الغرف</Text>
              <Text style={styles.specValue}>{apartment.roomsCount} غرف</Text>
            </View>
            <View style={styles.specItem}>
              <Text style={styles.specLabel}>الاستيعاب</Text>
              <Text style={styles.specValue}>{apartment.capacity} أشخاص</Text>
            </View>
          </View>

          <View style={styles.divider} />

          <Text style={styles.sectionTitle}>المرافق والخدمات</Text>
          <View style={styles.amenitiesGrid}>
            {Object.entries(apartment.amenities || {}).map(([key, value]) => (
              <View key={key} style={styles.amenityItem}>
                <Text style={styles.amenityText}>
                  {value ? '✅' : '❌'} {key}
                </Text>
              </View>
            ))}
          </View>

          <View style={styles.divider} />

          <Text style={styles.sectionTitle}>تفاصيل الحجز</Text>
          <View style={styles.dateInputs}>
            <View style={styles.dateField}>
              <Text style={styles.dateLabel}>تاريخ البدء (YYYY-MM-DD)</Text>
              <TextInput
                style={styles.dateInput}
                value={startDate}
                onChangeText={(val) => handleDateChange('start', val)}
                placeholder="YYYY-MM-DD"
                placeholderTextColor="#999"
              />
            </View>

            <View style={styles.dateField}>
              <Text style={styles.dateLabel}>تاريخ الانتهاء (YYYY-MM-DD)</Text>
              <TextInput
                style={styles.dateInput}
                value={endDate}
                onChangeText={(val) => handleDateChange('end', val)}
                placeholder="YYYY-MM-DD"
                placeholderTextColor="#999"
              />
            </View>
          </View>

          {totalPrice > 0 && (
            <View style={styles.pricingSummary}>
              <Text style={styles.summaryText}>إجمالي السعر: {totalPrice} د.ل</Text>
              <Text style={styles.pointsText}>النقاط المكتسبة: +{points} نقطة</Text>
            </View>
          )}

          <TouchableOpacity
            style={styles.bookButton}
            onPress={handleBooking}
            disabled={bookingLoading || totalPrice === 0}
          >
            {bookingLoading ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.bookButtonText}>تأكيد الحجز الآن</Text>
            )}
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#fff',
  },
  container: {
    paddingBottom: 40,
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
  },
  image: {
    width: '100%',
    height: 250,
  },
  infoCard: {
    padding: 20,
    backgroundColor: '#fff',
    borderTopLeftRadius: 25,
    borderTopRightRadius: 25,
    marginTop: -20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
  },
  region: {
    fontSize: 15,
    color: '#7f8c8d',
    textAlign: 'right',
    marginTop: 4,
  },
  price: {
    fontSize: 20,
    color: '#2ecc71',
    fontWeight: 'bold',
    textAlign: 'right',
    marginTop: 8,
  },
  divider: {
    height: 1,
    backgroundColor: '#ecf0f1',
    marginVertical: 15,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2c3e50',
    textAlign: 'right',
    marginBottom: 10,
  },
  specGrid: {
    flexDirection: 'row-reverse',
    justifyContent: 'space-between',
  },
  specItem: {
    alignItems: 'center',
    flex: 1,
    backgroundColor: '#f8f9fa',
    padding: 10,
    marginHorizontal: 4,
    borderRadius: 8,
  },
  specLabel: {
    fontSize: 12,
    color: '#7f8c8d',
    marginBottom: 4,
  },
  specValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#2c3e50',
  },
  amenitiesGrid: {
    flexDirection: 'row-reverse',
    flexWrap: 'wrap',
    justifyContent: 'flex-start',
  },
  amenityItem: {
    width: '50%',
    paddingVertical: 4,
  },
  amenityText: {
    fontSize: 14,
    color: '#2c3e50',
    textAlign: 'right',
  },
  dateInputs: {
    gap: 12,
  },
  dateField: {
    flex: 1,
  },
  dateLabel: {
    fontSize: 13,
    color: '#34495e',
    marginBottom: 6,
    textAlign: 'right',
  },
  dateInput: {
    backgroundColor: '#f1f2f6',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 15,
    color: '#2c3e50',
    textAlign: 'center',
  },
  pricingSummary: {
    backgroundColor: '#e8f8f5',
    borderRadius: 8,
    padding: 15,
    marginTop: 15,
    alignItems: 'center',
  },
  summaryText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#117a65',
  },
  pointsText: {
    fontSize: 13,
    color: '#16a085',
    marginTop: 4,
  },
  bookButton: {
    backgroundColor: '#e67e22',
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 20,
  },
  bookButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
