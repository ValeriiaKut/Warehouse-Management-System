package com.example.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.RetrofitClient
import com.example.frontend.state.UserState
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    var state by mutableStateOf(UserState())
        private set

    fun loadCurrentUser(token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val user = RetrofitClient.api.getCurrentUser("Bearer $token")
                state = state.copy(user = user, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun clear() {
        state = UserState()
    }
}
