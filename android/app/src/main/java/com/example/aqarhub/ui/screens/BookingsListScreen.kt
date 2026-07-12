package com.example.aqarhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aqarhub.ui.viewmodel.BookingsListState
import com.example.aqarhub.ui.viewmodel.BookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsListScreen(
    onBack: () -> Unit,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حجوزاتي") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val listState = state) {
                is BookingsListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BookingsListState.Error -> {
                    Text(
                        text = listState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BookingsListState.Success -> {
                    if (listState.bookings.isEmpty()) {
                        Text(
                            text = "لا توجد حجوزات سابقة.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listState.bookings) { booking ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = booking.apartment.title,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "📅 الفترة: من ${booking.startDate} إلى ${booking.endDate}")
                                        Text(text = "💰 التكلفة الإجمالية: ${booking.totalPrice} LYD")
                                        Text(text = "🌟 النقاط المكتسبة: ${booking.earnedPoints} نقطة")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(
                                                when(booking.status) {
                                                    "confirmed" -> "مؤكد"
                                                    "cancelled" -> "ملغي"
                                                    else -> "معلق"
                                                }
                                            ) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
