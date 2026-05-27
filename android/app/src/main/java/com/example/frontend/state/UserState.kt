package com.example.frontend.state

import com.example.frontend.data.CurrentUserResponse

data class UserState(
    val user: CurrentUserResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionExpired: Boolean = false
)
