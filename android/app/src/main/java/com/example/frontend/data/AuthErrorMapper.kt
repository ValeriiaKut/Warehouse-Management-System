package com.example.frontend.data

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

object AuthErrorMapper {
    fun isRateLimited(error: Throwable): Boolean {
        return (error as? HttpException)?.code() == 429
    }

    fun loginMessage(error: Throwable): String {
        val statusCode = (error as? HttpException)?.code()

        return when (statusCode) {
            401 -> "Invalid email or password."
            429 -> "Too many login attempts. Try again later."
            422 -> "Enter a valid email and password."
            else -> defaultMessage(error, "Unable to sign in. Please try again.")
        }
    }

    fun registerMessage(error: Throwable): String {
        val statusCode = (error as? HttpException)?.code()

        return when (statusCode) {
            400 -> "Username or email already exists."
            422 -> validationMessage(error)
            else -> defaultMessage(error, "Unable to create account. Please try again.")
        }
    }

    fun retryAfterSeconds(error: Throwable): Int {
        val httpError = error as? HttpException ?: return 60
        val retryAfter = httpError.response()?.headers()?.get("Retry-After")?.toIntOrNull()
        return retryAfter ?: 60
    }

    private fun validationMessage(error: Throwable): String {
        val detail = error.detailFromBody()

        return when {
            detail.contains("Password must have at least 8 characters", ignoreCase = true) ->
                "Password must be at least 8 characters long."
            detail.contains("Password must contain at least one number", ignoreCase = true) ->
                "Password must contain at least one number."
            detail.contains("email", ignoreCase = true) ->
                "Enter a valid email address."
            else -> "Check the entered data and try again."
        }
    }

    private fun defaultMessage(error: Throwable, fallback: String): String {
        return if (error is HttpException) {
            fallback
        } else {
            "Backend service is unavailable. Make sure the server is running."
        }
    }

    private fun Throwable.detailFromBody(): String {
        val httpError = this as? HttpException ?: return localizedMessage.orEmpty()
        val body = httpError.response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return localizedMessage.orEmpty()

        return runCatching {
            val detail = JSONObject(body).opt("detail")
            when (detail) {
                is String -> detail
                is JSONArray -> detail.toString()
                else -> body
            }
        }.getOrDefault(body)
    }
}
