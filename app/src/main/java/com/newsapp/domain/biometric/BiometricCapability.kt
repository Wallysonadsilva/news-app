package com.newsapp.domain.biometric

sealed class BiometricCapability {
    data object Available: BiometricCapability()
    data object NotAvailable: BiometricCapability()
    data object NoneEnrolled: BiometricCapability()
}
