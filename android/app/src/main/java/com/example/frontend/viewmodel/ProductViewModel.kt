package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.ProductCreate
import com.example.frontend.model.ProductModel
import com.example.frontend.model.ProductUpdate
import com.example.frontend.state.ProductState
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var state by mutableStateOf(ProductState())
        private set

    fun loadProducts(token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val products = RetrofitClient.api.getProducts("Bearer $token")
                state = state.copy(products = products, isLoading = false)

            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun addProduct(product: ProductModel, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val createdProduct = RetrofitClient.api.createProduct(
                    authorization = "Bearer $token",
                    product = ProductCreate(
                        name = product.name,
                        sku = product.sku,
                        quantity = product.quantity,
                        price = product.price,
                        description = product.description
                    )
                )

                state = state.copy(
                    products = state.products + createdProduct,
                    isLoading = false
                )
                onSuccess()
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateProduct(product: ProductModel, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val updatedProduct = RetrofitClient.api.updateProduct(
                    authorization = "Bearer $token",
                    productId = product.id,
                    product = ProductUpdate(
                        name = product.name,
                        sku = product.sku,
                        quantity = product.quantity,
                        price = product.price,
                        description = product.description
                    )
                )

                state = state.copy(
                    products = state.products.map {
                        if (it.id == updatedProduct.id) updatedProduct else it
                    },
                    isLoading = false
                )
                onSuccess()
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteProduct(productId: Int, token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                RetrofitClient.api.deleteProduct(
                    authorization = "Bearer $token",
                    productId = productId
                )

                state = state.copy(
                    products = state.products.filterNot { it.id == productId },
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }
}
