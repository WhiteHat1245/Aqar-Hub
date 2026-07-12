package com.example.aqarhub

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Login : NavKey
@Serializable data object ApartmentsList : NavKey
@Serializable data class ApartmentDetail(val id: Int) : NavKey
@Serializable data object BookingsList : NavKey
