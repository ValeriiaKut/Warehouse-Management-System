package com.example.frontend.data

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

object ApiErrorMapper {
    fun isUnauthorized(error: Throwable): Boolean {
        return (error as? HttpException)?.code() == 401
    }

    fun message(error: Throwable): String {
        val statusCode = (error as? HttpException)?.code()
        val detail = detailFromBody(error)

        return when {
            statusCode == 401 -> "Session expired. Please sign in again."
            statusCode == 403 -> "You do not have permission to perform this action."
            detail.contains("Product with this SKU already exists", ignoreCase = true) ->
                "A product with this SKU already exists."
            detail.contains("Cannot delete product because it is used in orders", ignoreCase = true) ->
                "This product is used in orders and cannot be deleted."
            detail.contains("Not enough stock for product", ignoreCase = true) ->
                detail
            detail.contains("Order must contain at least one item", ignoreCase = true) ->
                "Choose at least one product quantity."
            detail.contains("Quantity must be greater than 0", ignoreCase = true) ->
                "Quantity must be greater than zero."
            detail.contains("Invalid order status", ignoreCase = true) ->
                "Choose a valid order status."
            detail.contains("Order not found", ignoreCase = true) ->
                "Order not found."
            detail.contains("Product not found", ignoreCase = true) ->
                "Product not found."
            error is HttpException -> "Request failed. Please try again."
            else -> "Backend service is unavailable. Make sure the server is running."
        }
    }

    fun detailFromBody(error: Throwable): String {
        val httpError = error as? HttpException ?: return error.localizedMessage.orEmpty()
        val body = httpError.response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return error.localizedMessage.orEmpty()

        return runCatching {
            when (val detail = JSONObject(body).opt("detail")) {
                is String -> detail
                is JSONArray -> detail.toString()
                else -> body
            }
        }.getOrDefault(body)
    }
}
