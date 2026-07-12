package com.example.aqarhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aqarhub.ui.components.ApartmentCard
import com.example.aqarhub.ui.viewmodel.ApartmentsState
import com.example.aqarhub.ui.viewmodel.ApartmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentsListScreen(
    onApartmentClick: (Int) -> Unit,
    onNavigateToBookings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    var region by remember { mutableStateOf("") }
    var maxPriceStr by remember { mutableStateOf("") }

    val state by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadApartments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استكشف العقارات") },
                actions = {
                    IconButton(onClick = onNavigateToBookings) {
                        Icon(Icons.Default.List, contentDescription = "حجوزاتي")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "خروج")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("المنطقة") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = maxPriceStr,
                    onValueChange = { maxPriceStr = it },
                    label = { Text("السعر الأقصى") },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val maxPrice = maxPriceStr.toDoubleOrNull()
                        viewModel.loadApartments(region, maxPrice)
                    }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "بحث")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val listState = state) {
                is ApartmentsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ApartmentsState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = listState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ApartmentsState.Success -> {
                    if (listState.apartments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "لا توجد عقارات مطابقة للبحث.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listState.apartments) { apartment ->
                                ApartmentCard(
                                    apartment = apartment,
                                    onCardClick = onApartmentClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
