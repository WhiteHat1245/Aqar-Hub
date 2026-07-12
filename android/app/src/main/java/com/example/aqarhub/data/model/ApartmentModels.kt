package com.example.aqarhub.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApartmentResponse(
    val id: Int,
    val title: String,
    val type: String,
    val region: String,
    val basePrice: Double,
    val roomsCount: Int,
    val capacity: Int,
    val amenities: Map<String, Boolean> = emptyMap(),
    val images: List<String> = emptyList(),
    val isActive: Boolean = true
)
