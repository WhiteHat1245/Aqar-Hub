package com.example.aqarhub.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val apartmentId: Int,
    val startDate: String,
    val endDate: String
)

@Serializable
data class BookingResponse(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val earnedPoints: Int = 0,
    val user: UserResponse? = null,
    val apartment: ApartmentResponse
)
