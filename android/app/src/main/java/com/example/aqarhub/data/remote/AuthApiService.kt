package com.example.aqarhub.data.remote

import com.example.aqarhub.data.model.LoginRequest
import com.example.aqarhub.data.model.LoginResponse
import com.example.aqarhub.data.model.RegisterRequest
import com.example.aqarhub.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>
}
