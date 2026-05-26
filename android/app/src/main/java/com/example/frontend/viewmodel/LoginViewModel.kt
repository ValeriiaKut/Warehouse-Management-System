package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.AuthErrorMapper
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.LoginModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var retrySeconds by mutableStateOf(0)

    private var retryTimerJob: Job? = null


    fun login(loginData: LoginModel, onSuccess: (String) -> Unit) {
        if (retrySeconds > 0) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.authApi.login(loginData)
                onSuccess(response.accessToken)
            } catch (e: Exception) {
                errorMessage = AuthErrorMapper.loginMessage(e)
                if (AuthErrorMapper.isRateLimited(e)) {
                    startRetryTimer(AuthErrorMapper.retryAfterSeconds(e))
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun startRetryTimer(seconds: Int) {
        retryTimerJob?.cancel()
        retryTimerJob = viewModelScope.launch {
            retrySeconds = seconds.coerceAtLeast(1)
            while (retrySeconds > 0) {
                delay(1000)
                retrySeconds -= 1
            }
        }
    }
}
