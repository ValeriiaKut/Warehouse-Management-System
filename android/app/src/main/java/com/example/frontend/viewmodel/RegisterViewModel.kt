package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.AuthErrorMapper
import com.example.frontend.data.RetrofitClient
import com.example.frontend.model.RegisterModel
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun register(registerData: RegisterModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                RetrofitClient.authApi.register(registerData)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = AuthErrorMapper.registerMessage(e)
            } finally {
                isLoading = false
            }
        }
    }
}
