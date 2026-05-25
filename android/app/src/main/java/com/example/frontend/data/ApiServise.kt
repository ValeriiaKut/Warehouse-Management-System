package com.example.frontend.data

import com.example.frontend.model.LoginModel
import com.example.frontend.model.OrderCreate
import com.example.frontend.model.OrderModel
import com.example.frontend.model.ProductCreate
import com.example.frontend.model.ProductModel
import com.example.frontend.model.ProductUpdate
import com.example.frontend.model.RegisterModel
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginModel): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterModel): RegisterResponse

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): CurrentUserResponse

    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
        @Query("search") search: String? = null,
        @Query("min_price") minPrice: Float? = null,
        @Query("max_price") maxPrice: Float? = null,
        @Query("low_stock") lowStock: Boolean = false
    ): List<ProductModel>

    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") authorization: String,
        @Body product: ProductCreate
    ): ProductModel

    @PUT("products/{productId}")
    suspend fun updateProduct(
        @Header("Authorization") authorization: String,
        @Path("productId") productId: Int,
        @Body product: ProductUpdate
    ): ProductModel

    @DELETE("products/{productId}")
    suspend fun deleteProduct(
        @Header("Authorization") authorization: String,
        @Path("productId") productId: Int
    )

    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") authorization: String
    ): List<OrderModel>

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Body order: OrderCreate
    ): OrderModel

    @PUT("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") authorization: String,
        @Path("orderId") orderId: Int,
        @Query("status") status: String
    ): OrderModel
}

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String
)

data class RegisterResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String
)

data class CurrentUserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String
)
