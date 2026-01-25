package com.newsapp.presentation.biometric

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.data.biometric.BiometricAuthManager
import com.newsapp.domain.biometric.BiometricAuthResult
import com.newsapp.domain.biometric.BiometricCapability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricAuthViewModel @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    var isAuthenticated by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticating by mutableStateOf(false)
        private set

    var requiresAuth by mutableStateOf(false)
        private set

    init {
        val capability = biometricAuthManager.checkBiometricCapability()
        requiresAuth = capability == BiometricCapability.Available

        if (!requiresAuth) {
            isAuthenticated = true
        }
    }

    fun checkBiometricCapability(): BiometricCapability {
        return biometricAuthManager.checkBiometricCapability()
    }

    fun authenticate(activity: FragmentActivity) {
        viewModelScope.launch {
            isAuthenticating = true
            errorMessage = null

            when (val result = biometricAuthManager.authenticate(activity)) {
                is BiometricAuthResult.Success -> {
                    isAuthenticated = true
                    errorMessage = null
                }
                is BiometricAuthResult.Failed -> {
                    errorMessage = "Fingerprint not recognized. Try again."
                }
                is BiometricAuthResult.Error -> {
                    errorMessage = result.message
                }
            }

            isAuthenticating = false
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
