package com.example.frontend.data

import com.example.frontend.model.LoginModel
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

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginModel): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterModel): RegisterResponse

    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String
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
    val email: String
)
