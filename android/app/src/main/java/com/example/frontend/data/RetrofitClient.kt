package com.example.frontend.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val AUTH_BASE_URL = "http://10.0.2.2:8001/"
    private const val INVENTORY_BASE_URL = "http://10.0.2.2:8002/"
    private const val ORDERS_BASE_URL = "http://10.0.2.2:8003/"

    val authApi: ApiService by lazy {
        createApi(AUTH_BASE_URL)
    }

    val inventoryApi: ApiService by lazy {
        createApi(INVENTORY_BASE_URL)
    }

    val ordersApi: ApiService by lazy {
        createApi(ORDERS_BASE_URL)
    }

    private fun createApi(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
