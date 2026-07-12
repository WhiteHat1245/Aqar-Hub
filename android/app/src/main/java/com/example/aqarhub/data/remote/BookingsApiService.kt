package com.example.aqarhub.data.remote

import com.example.aqarhub.data.model.BookingResponse
import com.example.aqarhub.data.model.CreateBookingRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BookingsApiService {
    @POST("bookings")
    suspend fun createBooking(
        @Body request: CreateBookingRequest
    ): Response<BookingResponse>

    @GET("bookings")
    suspend fun getUserBookings(): List<BookingResponse>
}
