package com.example.frontend.data

import com.example.frontend.model.LoginModel
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginModel): LoginResponse
}

data class LoginResponse(
    val accessToken: String,
    val tokenType: String
)