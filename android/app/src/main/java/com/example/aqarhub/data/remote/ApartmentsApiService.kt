package com.example.aqarhub.data.remote

import com.example.aqarhub.data.model.ApartmentResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApartmentsApiService {
    @GET("apartments")
    suspend fun getApartments(
        @Query("region") region: String?,
        @Query("maxPrice") maxPrice: Double?
    ): List<ApartmentResponse>

    @GET("apartments/{id}")
    suspend fun getApartmentById(
        @Path("id") id: Int
    ): ApartmentResponse

    @Multipart
    @POST("apartments/{id}/images")
    suspend fun uploadApartmentImages(
        @Path("id") apartmentId: Int,
        @Part images: List<MultipartBody.Part>
    ): ApartmentResponse
}
