package com.newsapp.domain.biometric

sealed class BiometricAuthResult {
    data object Success: BiometricAuthResult()
    data object Failed: BiometricAuthResult()
    data class Error(val message: String): BiometricAuthResult()
}
