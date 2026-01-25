package com.newsapp.presentation

import androidx.fragment.app.FragmentActivity
import com.newsapp.data.biometric.BiometricAuthManager
import com.newsapp.domain.biometric.BiometricAuthResult
import com.newsapp.domain.biometric.BiometricCapability
import com.newsapp.presentation.biometric.BiometricAuthViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricAuthViewModelTest {

    private lateinit var biometricAuthManager: BiometricAuthManager
    private lateinit var viewModel: BiometricAuthViewModel
    private lateinit var mockActivity: FragmentActivity

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        biometricAuthManager = mockk()
        mockActivity = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init with biometric available sets requiresAuth to true`() {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available

        // WHEN
        viewModel = BiometricAuthViewModel(biometricAuthManager)

        // THEN
        Assert.assertTrue(viewModel.requiresAuth)
        Assert.assertFalse(viewModel.isAuthenticated)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `init with biometric not available skips authentication`() {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.NotAvailable

        // WHEN
        viewModel = BiometricAuthViewModel(biometricAuthManager)

        // THEN
        Assert.assertFalse(viewModel.requiresAuth)
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `init with biometric none enrolled skips authentication`() {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.NoneEnrolled

        // WHEN
        viewModel = BiometricAuthViewModel(biometricAuthManager)

        // THEN
        Assert.assertFalse(viewModel.requiresAuth)
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `authenticate with success updates state correctly`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Success

        // WHEN
        viewModel.authenticate(mockActivity)

        // THEN
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertFalse(viewModel.isAuthenticating)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `authenticate with failed result sets error message`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Failed

        // WHEN
        viewModel.authenticate(mockActivity)

        // THEN
        Assert.assertFalse(viewModel.isAuthenticated)
        Assert.assertFalse(viewModel.isAuthenticating)
        Assert.assertEquals("Fingerprint not recognized. Try again.", viewModel.errorMessage)
    }

    @Test
    fun `authenticate with error result sets custom error message`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        val errorMsg = "Biometric sensor not available"
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Error(errorMsg)

        // WHEN
        viewModel.authenticate(mockActivity)

        // THEN
        Assert.assertFalse(viewModel.isAuthenticated)
        Assert.assertFalse(viewModel.isAuthenticating)
        Assert.assertEquals(errorMsg, viewModel.errorMessage)
    }

    @Test
    fun `authenticate sets isAuthenticating to true during execution`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Success

        // WHEN
        viewModel.authenticate(mockActivity)

        // THEN
        Assert.assertFalse(viewModel.isAuthenticating)
        Assert.assertTrue(viewModel.isAuthenticated)
    }

    @Test
    fun `clearError removes error message`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Error("Test error")
        viewModel.authenticate(mockActivity)
        Assert.assertNotNull(viewModel.errorMessage)

        // WHEN
        viewModel.clearError()

        // THEN
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `authenticate clears previous error before starting`() = runTest {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Error("First error")
        viewModel.authenticate(mockActivity)
        Assert.assertEquals("First error", viewModel.errorMessage)

        // WHEN
        coEvery { biometricAuthManager.authenticate(mockActivity) } returns BiometricAuthResult.Success
        viewModel.authenticate(mockActivity)

        // THEN
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `requiresAuth remains consistent throughout lifecycle`() {
        // GIVEN
        every { biometricAuthManager.checkBiometricCapability() } returns BiometricCapability.Available
        viewModel = BiometricAuthViewModel(biometricAuthManager)

        // THEN
        Assert.assertTrue(viewModel.requiresAuth)

        // requiresAuth should not change even after other operations
        Assert.assertTrue(viewModel.requiresAuth)
    }
}
