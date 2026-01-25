package com.newsapp.data.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.newsapp.domain.biometric.BiometricAuthResult
import com.newsapp.domain.biometric.BiometricCapability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun checkBiometricCapability(): BiometricCapability {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricCapability.Available
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricCapability.NoneEnrolled
            else -> BiometricCapability.NotAvailable
        }
    }

    suspend fun authenticate(activity: FragmentActivity): BiometricAuthResult = suspendCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    continuation.resume(BiometricAuthResult.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val errorMessage = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> "Authentication required to continue"
                        else -> errString.toString()
                    }
                    continuation.resume(BiometricAuthResult.Error(errorMessage))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    continuation.resume(BiometricAuthResult.Failed)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock News App")
            .setSubtitle("Authentication required")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
