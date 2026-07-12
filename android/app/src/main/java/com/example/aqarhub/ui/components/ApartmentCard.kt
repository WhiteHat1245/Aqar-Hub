package com.example.aqarhub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aqarhub.data.model.ApartmentResponse
import com.example.aqarhub.utils.Constants

@Composable
fun ApartmentCard(
    apartment: ApartmentResponse,
    onCardClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick(apartment.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = apartment.images.firstOrNull()?.let { "${Constants.BASE_URL}$it" },
                contentDescription = "Apartment Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = apartment.title, style = MaterialTheme.typography.titleLarge)
                Text(text = "📍 ${apartment.region}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${apartment.basePrice} LYD / ليلة",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
