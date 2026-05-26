package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.ApiErrorMapper
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.ProductCreate
import com.example.frontend.model.ProductModel
import com.example.frontend.model.ProductUpdate
import com.example.frontend.state.ProductState
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var state by mutableStateOf(ProductState())
        private set

    fun loadProducts(
        token: String,
        search: String? = null,
        minPrice: Float? = null,
        maxPrice: Float? = null,
        lowStock: Boolean = false
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val products = RetrofitClient.inventoryApi.getProducts(
                    authorization = "Bearer $token",
                    search = search?.takeIf { it.isNotBlank() },
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    lowStock = lowStock
                )
                state = state.copy(products = products, isLoading = false)

            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun addProduct(product: ProductModel, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val createdProduct = RetrofitClient.inventoryApi.createProduct(
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
                    isLoading = false,
                    successMessage = "Product created successfully."
                )
                onSuccess()
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun updateProduct(product: ProductModel, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                val updatedProduct = RetrofitClient.inventoryApi.updateProduct(
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
                    isLoading = false,
                    successMessage = "Product updated successfully."
                )
                onSuccess()
            } catch (e: Exception) {
                setError(e)
            }
        }
    }

    fun deleteProduct(productId: Int, token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, successMessage = null)

            try {
                RetrofitClient.inventoryApi.deleteProduct(
                    authorization = "Bearer $token",
                    productId = productId
                )

                state = state.copy(
                    products = state.products.filterNot { it.id == productId },
                    isLoading = false,
                    successMessage = "Product deleted successfully."
                )
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
