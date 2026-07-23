@file:JvmName("SharedDashboardScreen")

package com.example.aqarhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aqarhub.data.model.ApartmentResponse
import com.example.aqarhub.theme.*
import com.example.aqarhub.ui.components.ApartmentCard
import com.example.aqarhub.ui.viewmodel.DashboardUiState
import com.example.aqarhub.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onApartmentClick: (Int) -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = EarthPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "عقار هب",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EarthPrimaryDark
                            )
                        }
                    },
                    actions = {
                        // Refresh button
                        IconButton(onClick = { viewModel.fetchApartments() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "تحديث",
                                tint = EarthPrimary
                            )
                        }
                        // Logout button
                        IconButton(onClick = {
                            viewModel.logout()
                            onLogoutClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "تسجيل الخروج",
                                tint = EarthError
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = EarthBg
                    )
                )
            },
            containerColor = EarthBg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val state = uiState) {
                    is DashboardUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = EarthPrimary)
                        }
                    }

                    is DashboardUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = state.message,
                                    color = EarthError,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Button(
                                    onClick = { viewModel.fetchApartments() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EarthPrimary)
                                ) {
                                    Text("إعادة المحاولة", color = Color.White)
                                }
                            }
                        }
                    }

                    is DashboardUiState.Success -> {
                        if (state.apartments.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد عقارات متاحة حالياً.",
                                    color = EarthTextMedium,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            // Responsive Layout Detection using BoxWithConstraints
                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val isDesktop = maxWidth > 600.dp

                                if (isDesktop) {
                                    // Desktop / Wide Screen: Adaptive Grid Layout
                                    LazyVerticalGrid(
                                        columns = GridCells.Adaptive(minSize = 300.dp),
                                        contentPadding = PaddingValues(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(state.apartments, key = { it.id }) { apartment ->
                                            ApartmentCard(
                                                apartment = apartment,
                                                onClick = { onApartmentClick(apartment.id) },
                                                onBookClick = { onApartmentClick(apartment.id) }
                                            )
                                        }
                                    }
                                } else {
                                    // Mobile Screen: Vertical List Layout
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(state.apartments, key = { it.id }) { apartment ->
                                            ApartmentCard(
                                                apartment = apartment,
                                                onClick = { onApartmentClick(apartment.id) },
                                                onBookClick = { onApartmentClick(apartment.id) }
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
}
