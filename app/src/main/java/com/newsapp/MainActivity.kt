package com.newsapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.newsapp.navigation.NewsNavGraph
import com.newsapp.presentation.biometric.BiometricAuthViewModel
import com.newsapp.presentation.biometric.LockScreen
import com.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val biometricViewModel: BiometricAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(Unit) {
                        if (biometricViewModel.requiresAuth && !biometricViewModel.isAuthenticated) {
                            biometricViewModel.authenticate(this@MainActivity)
                        }
                    }

                    if(biometricViewModel.isAuthenticated){
                        val navController = rememberNavController()
                        NewsNavGraph(navController = navController)
                    } else {
                        LockScreen(
                            onAuthenticateClick = {
                                biometricViewModel.authenticate(this@MainActivity)
                                                  },
                            errorMessage = biometricViewModel.errorMessage,
                            isAuthenticating = biometricViewModel.isAuthenticating
                        )
                    }
                }
            }
        }
    }
}
