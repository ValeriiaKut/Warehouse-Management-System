package com.example.frontend.state

import com.example.frontend.model.OrderModel

data class OrderState(
    val orders: List<OrderModel> = emptyList(),
    val currentUserRole: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val sessionExpired: Boolean = false
)
