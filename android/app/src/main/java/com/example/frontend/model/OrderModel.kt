package com.example.frontend.model

import com.google.gson.annotations.SerializedName

data class OrderItemCreate(
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int
)

data class OrderCreate(
    val items: List<OrderItemCreate>
)

data class OrderItemModel(
    val id: Int,
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int
)

data class OrderModel(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    val items: List<OrderItemModel>
)
