package com.example.frontend.model


data class ProductCreate(
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Float,
    val description: String?
)

data class ProductUpdate(
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Float,
    val description: String?
)
