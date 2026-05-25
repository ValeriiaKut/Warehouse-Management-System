package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.OrderCreate
import com.example.frontend.model.OrderItemCreate
import com.example.frontend.state.OrderState
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderViewModel : ViewModel() {
    var state by mutableStateOf(OrderState())
        private set

    fun loadCurrentUser(token: String) {
        viewModelScope.launch {
            try {
                val user = RetrofitClient.api.getCurrentUser("Bearer $token")
                state = state.copy(currentUserRole = user.role)
            } catch (e: Exception) {
                state = state.copy(error = e.readableMessage())
            }
        }
    }

    fun loadOrders(token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val orders = RetrofitClient.api.getOrders("Bearer $token")
                state = state.copy(orders = orders, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(error = e.readableMessage(), isLoading = false)
            }
        }
    }

    fun createOrder(items: List<OrderItemCreate>, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val createdOrder = RetrofitClient.api.createOrder(
                    authorization = "Bearer $token",
                    order = OrderCreate(items = items)
                )

                state = state.copy(
                    orders = listOf(createdOrder) + state.orders,
                    isLoading = false
                )
                onSuccess()
            } catch (e: Exception) {
                state = state.copy(error = e.readableMessage(), isLoading = false)
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String, token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val updatedOrder = RetrofitClient.api.updateOrderStatus(
                    authorization = "Bearer $token",
                    orderId = orderId,
                    status = status
                )

                state = state.copy(
                    orders = state.orders.map {
                        if (it.id == updatedOrder.id) updatedOrder else it
                    },
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(error = e.readableMessage(), isLoading = false)
            }
        }
    }

    private fun Exception.readableMessage(): String {
        if (this is HttpException) {
            return response()?.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Request failed with code ${code()}"
        }

        return message ?: "Something went wrong"
    }
}
