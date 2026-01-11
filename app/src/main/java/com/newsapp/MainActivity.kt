package com.newsapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.newsapp.navigation.NewsNavGraph
import com.newsapp.presentation.LockScreen
import com.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Checks if biometric is available
                    val biometricManager = BiometricManager.from(this)
                    val canAuthenticate = biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                    )

                    val requiresAuth = canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS

                    var isAuthenticated by remember { mutableStateOf(!requiresAuth) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    var shouldShowPrompt by remember { mutableStateOf(requiresAuth) }

                    LaunchedEffect(shouldShowPrompt) {
                        if (shouldShowPrompt) {
                            shouldShowPrompt = false
                            showBiometricPrompt(
                                onSuccess = {
                                    isAuthenticated = true
                                    errorMessage = null
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
                        }
                    }

                    if (isAuthenticated) {
                        val navController = rememberNavController()
                        NewsNavGraph(navController = navController)
                    } else {
                        LockScreen(
                            onAuthenticateClick = {
                                showBiometricPrompt(
                                    onSuccess = {
                                        isAuthenticated = true
                                        errorMessage = null
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            },
                            errorMessage = errorMessage
                        )
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onError("Authentication required to continue")
                    } else {
                        onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Fingerprint not recognised. Try again.")
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
