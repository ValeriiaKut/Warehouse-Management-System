package com.example.frontend.state

import com.example.frontend.model.ProductModel

data class ProductState(
    val products: List<ProductModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val sessionExpired: Boolean = false
)
