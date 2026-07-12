package com.example.aqarhub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aqarhub.ui.viewmodel.ApartmentDetailState
import com.example.aqarhub.ui.viewmodel.ApartmentsViewModel
import com.example.aqarhub.ui.viewmodel.CreateBookingState
import com.example.aqarhub.ui.viewmodel.BookingsViewModel
import com.example.aqarhub.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentDetailScreen(
    apartmentId: Int,
    onBack: () -> Unit,
    apartmentsViewModel: ApartmentsViewModel = hiltViewModel(),
    bookingsViewModel: BookingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val detailState by apartmentsViewModel.detailState.collectAsState()
    val createBookingState by bookingsViewModel.createState.collectAsState()

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    LaunchedEffect(apartmentId) {
        apartmentsViewModel.loadApartmentDetail(apartmentId)
    }

    LaunchedEffect(createBookingState) {
        if (createBookingState is CreateBookingState.Success) {
            Toast.makeText(context, "تم الحجز بنجاح!", Toast.LENGTH_SHORT).show()
            bookingsViewModel.resetCreateState()
            onBack()
        } else if (createBookingState is CreateBookingState.Error) {
            Toast.makeText(context, (createBookingState as CreateBookingState.Error).message, Toast.LENGTH_LONG).show()
            bookingsViewModel.resetCreateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل العقار") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = detailState) {
            is ApartmentDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ApartmentDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ApartmentDetailState.Success -> {
                val apartment = state.apartment
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = apartment.images.firstOrNull()?.let { "${Constants.BASE_URL}$it" },
                        contentDescription = "Apartment Detail Image",
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = apartment.title, style = MaterialTheme.typography.headlineMedium)
                        Text(text = "📍 المنطقة: ${apartment.region}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "${apartment.basePrice} LYD / ليلة",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text(text = "معلومات العقار:", style = MaterialTheme.typography.titleMedium)
                        Text(text = "• النوع: ${apartment.type}")
                        Text(text = "• عدد الغرف: ${apartment.roomsCount}")
                        Text(text = "• القدرة الاستيعابية: ${apartment.capacity} أشخاص")

                        if (apartment.amenities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "المرافق والخدمات:", style = MaterialTheme.typography.titleMedium)
                            apartment.amenities.forEach { (amenity, available) ->
                                if (available) {
                                    Text(text = "✔️ $amenity")
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text(text = "احجز الآن:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("تاريخ البدء (YYYY-MM-DD)") },
                            placeholder = { Text("مثال: 2026-07-20") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("تاريخ الانتهاء (YYYY-MM-DD)") },
                            placeholder = { Text("مثال: 2026-07-25") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (startDate.isNotBlank() && endDate.isNotBlank()) {
                                    bookingsViewModel.createBooking(apartment.id, startDate, endDate)
                                } else {
                                    Toast.makeText(context, "الرجاء تعبئة تواريخ الحجز", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = createBookingState !is CreateBookingState.Loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (createBookingState is CreateBookingState.Loading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text("تأكيد الحجز")
                            }
                        }
                    }
                }
            }
        }
    }
}
