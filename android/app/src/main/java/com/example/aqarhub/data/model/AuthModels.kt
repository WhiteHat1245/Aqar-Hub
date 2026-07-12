package com.example.aqarhub.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val role: String,
    val loyaltyPoints: Int = 0
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val user: UserResponse? = null
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null,
    val role: String = "client"
)
