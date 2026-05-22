package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.model.ProductModel
import com.example.frontend.state.ProductState
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var state by mutableStateOf(ProductState())
        private set

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                // TODO: API call
                // val products = api.getProducts()

                val fakeData = listOf(
                    ProductModel(1, "Laptop", "SKU123", 10, 999.99f, "Gaming laptop"),
                    ProductModel(2, "Mouse", "SKU124", 50, 29.99f, "Wireless mouse")
                )

                state = state.copy(products = fakeData, isLoading = false)

            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }
}