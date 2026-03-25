package com.example.frontend.model

data class RegisterModel(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val username: String
)
