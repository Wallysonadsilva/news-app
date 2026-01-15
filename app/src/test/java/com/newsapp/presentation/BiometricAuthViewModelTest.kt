package com.newsapp.presentation

import com.appmattus.kotlinfixture.kotlinFixture
import com.newsapp.presentation.biometric.BiometricAuthViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BiometricAuthViewModelTest {

    private val fixture = kotlinFixture()
    private lateinit var viewModel: BiometricAuthViewModel

    @Before
    fun setUp() {
        viewModel = BiometricAuthViewModel()
    }

    @Test
    fun `initial state is unauthenticated and requests auth`() {

        Assert.assertFalse(viewModel.isAuthenticated)
        Assert.assertTrue(viewModel.shouldRequestAuth)
        Assert.assertNull(viewModel.errorMessage)
    }


    @Test
    fun `onAuthSuccess authenticates user and clears error`() {

        // WHEN
        viewModel.onAuthSuccess()

        // THEN
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertFalse(viewModel.shouldRequestAuth)
        Assert.assertNull(viewModel.errorMessage)
    }

    @Test
    fun `onAuthError sets error and stops auth request`() {
        // GIVEN
        val errorMessage = fixture<String>()

        // WHEN
        viewModel.onAuthError(errorMessage)

        // THEN
        Assert.assertFalse(viewModel.isAuthenticated)
        Assert.assertFalse(viewModel.shouldRequestAuth)
        Assert.assertEquals(errorMessage, viewModel.errorMessage)
    }

    @Test
    fun `requestAuth enables auth request`() {
        // GIVEN
        viewModel.onAuthError(fixture<String>())
        Assert.assertFalse(viewModel.shouldRequestAuth)

        // WHEN
        viewModel.requestAuth()

        // THEN
        Assert.assertTrue(viewModel.shouldRequestAuth)
    }

    @Test
    fun `complete authentication flow - error then success`() {
        // GIVEN
        val errorMessage = fixture<String>()
        Assert.assertTrue(viewModel.shouldRequestAuth)
        Assert.assertFalse(viewModel.isAuthenticated)

        // WHEN
        viewModel.onAuthError(errorMessage)

        // THEN
        Assert.assertEquals(errorMessage, viewModel.errorMessage)
        Assert.assertFalse(viewModel.shouldRequestAuth)
        Assert.assertFalse(viewModel.isAuthenticated)

        // WHEN
        viewModel.requestAuth()

        Assert.assertTrue(viewModel.shouldRequestAuth)

        // WHEN
        viewModel.onAuthSuccess()

        // THEN
        Assert.assertTrue(viewModel.isAuthenticated)
        Assert.assertNull(viewModel.errorMessage)
        Assert.assertFalse(viewModel.shouldRequestAuth)
    }
}
