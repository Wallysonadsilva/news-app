package com.newsapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BiometricAuthViewModel @Inject constructor() : ViewModel() {

    var isAuthenticated by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var shouldRequestAuth by mutableStateOf(true)
        private set

    fun requestAuth() {
        shouldRequestAuth = true
    }

    fun onAuthSuccess() {
        isAuthenticated = true
        errorMessage = null
        shouldRequestAuth = false
    }

    fun onAuthError(message: String) {
        errorMessage = message
        shouldRequestAuth = false
    }
}
