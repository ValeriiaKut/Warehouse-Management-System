package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.ApiErrorMapper
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.OrderCreate
import com.example.frontend.model.OrderItemCreate
import com.example.frontend.state.OrderState
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    var state by mutableStateOf(OrderState())
        private set

    fun loadCurrentUser(token: String) {
        viewModelScope.launch {
            try {
                val user = RetrofitClient.authApi.getCurrentUser("Bearer $token")
                state = state.copy(currentUserRole = user.role)
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun loadOrders(token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val orders = RetrofitClient.ordersApi.getOrders("Bearer $token")
                state = state.copy(orders = orders, isLoading = false)
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun createOrder(items: List<OrderItemCreate>, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val createdOrder = RetrofitClient.ordersApi.createOrder(
                    authorization = "Bearer $token",
                    order = OrderCreate(items = items)
                )

                state = state.copy(
                    orders = listOf(createdOrder) + state.orders,
                    isLoading = false,
                    successMessage = "Order created successfully."
                )
                onSuccess()
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String, token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val updatedOrder = RetrofitClient.ordersApi.updateOrderStatus(
                    authorization = "Bearer $token",
                    orderId = orderId,
                    status = status
                )

                state = state.copy(
                    orders = state.orders.map {
                        if (it.id == updatedOrder.id) updatedOrder else it
                    },
                    isLoading = false,
                    successMessage = "Order status updated."
                )
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun deleteOrder(orderId: Int, token: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                RetrofitClient.ordersApi.deleteOrder(
                    authorization = "Bearer $token",
                    orderId = orderId
                )

                state = state.copy(
                    orders = state.orders.filterNot { it.id == orderId },
                    isLoading = false,
                    successMessage = "Order deleted successfully."
                )
                onSuccess()
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun resetSessionExpired() {
        state = state.copy(sessionExpired = false)
    }

    private fun setError(error: Exception) {
        state = state.copy(
            error = ApiErrorMapper.message(error),
            isLoading = false,
            sessionExpired = ApiErrorMapper.isUnauthorized(error)
        )
    }
}
