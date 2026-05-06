package com.example.frontend.model

data class ProductModel(
    val id: Int,
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Float,
    val description: String?
)