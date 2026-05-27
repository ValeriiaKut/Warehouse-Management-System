package com.example.frontend.data

import com.example.frontend.model.ProductCreate
import com.example.frontend.model.ProductModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductApi {

    @GET("/products")
    suspend fun getProducts(): List<ProductModel>

    @POST("/products")
    suspend fun createProduct(@Body product: ProductCreate): ProductModel
}