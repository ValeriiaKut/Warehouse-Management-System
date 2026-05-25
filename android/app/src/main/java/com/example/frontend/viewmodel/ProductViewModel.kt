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
import retrofit2.HttpException

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
            state = state.copy(isLoading = true, error = null)

            try {
                val products = RetrofitClient.api.getProducts(
                    authorization = "Bearer $token",
                    search = search?.takeIf { it.isNotBlank() },
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    lowStock = lowStock
                )
                state = state.copy(products = products, isLoading = false)

            } catch (e: Exception) {
                state = state.copy(error = e.readableMessage(), isLoading = false)
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
                state = state.copy(error = e.readableMessage(), isLoading = false)
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
                state = state.copy(error = e.readableMessage(), isLoading = false)
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
