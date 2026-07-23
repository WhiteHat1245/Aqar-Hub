@file:JvmName("SharedApartmentCard")

package com.example.aqarhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aqarhub.data.model.ApartmentResponse
import com.example.aqarhub.theme.*

@Composable
fun ApartmentCard(
    apartment: ApartmentResponse,
    onClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Property Main Image & Badges Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(EarthBgWarm)
            ) {
                val imageUrl = apartment.images.firstOrNull()
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = apartment.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(EarthPrimary, EarthPrimaryDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Active Status Badge (Top Start)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (apartment.isActive) EarthSuccess else Color.Gray,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (apartment.isActive) "متاح للجز" else "غير متاح",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Type Badge (Top End)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EarthPrimaryDark.copy(alpha = 0.85f),
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = when (apartment.type) {
                            "villa" -> "فيلا"
                            "apartment" -> "شقة"
                            "chalet" -> "شاليه"
                            else -> apartment.type
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Card Body Information
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = apartment.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = EarthTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Region Location Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "المنطقة",
                        tint = EarthPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = apartment.region,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EarthTextMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Capacity info
                    Text(
                        text = "${apartment.roomsCount} غرف • ${apartment.capacity} ضيوف",
                        fontSize = 12.sp,
                        color = EarthTextMuted
                    )
                }

                HorizontalDivider(color = EarthTextMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                // Price and Quick Action Button Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "${apartment.basePrice.toInt()} ر.س",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EarthPrimaryDark
                        )
                        Text(
                            text = "/ ليلة واحدة",
                            fontSize = 11.sp,
                            color = EarthTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EarthPrimary),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "التفاصيل والحجز",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
