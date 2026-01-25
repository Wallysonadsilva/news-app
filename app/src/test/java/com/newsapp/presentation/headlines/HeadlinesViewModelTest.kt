package com.newsapp.presentation.headlines

import com.appmattus.kotlinfixture.kotlinFixture
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState
import com.newsapp.domain.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HeadlinesViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val fixture = kotlinFixture()

    private lateinit var repository: NewsRepository
    private lateinit var viewModel: HeadlinesViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `init loads headlines and emits success`() = runTest {
        // GIVEN
        val fakeArticles = fixture<List<Article>>()
        val successState = UiState.Success(fakeArticles)
        coEvery { repository.getTopHeadlines(any()) } returns successState

        // WHEN
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        // THEN
        assertEquals(successState, viewModel.uiState.value)
    }


    @Test
    fun `init loads headlines and emits error`() = runTest {
        // GIVEN
        val errorMessage = fixture<String>()
        val errorState = UiState.Error(errorMessage)
        coEvery { repository.getTopHeadlines(any()) } returns errorState

        // WHEN
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        // THEN
        assertEquals(errorState, viewModel.uiState.value)
    }

    @Test
    fun `retry triggers repository call again`() = runTest {
        // GIVEN
        val fakeArticles = fixture<List<Article>>()
        coEvery { repository.getTopHeadlines(any()) } returns UiState.Success(fakeArticles)
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        // WHEN
        viewModel.retry()
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 2) {
            repository.getTopHeadlines(any())
        }
    }

    @Test
    fun `loadHeadlines handles error after successful load`() = runTest {
        // GIVEN
        val fakeArticles = fixture<List<Article>>()
        coEvery { repository.getTopHeadlines(any()) } returns UiState.Success(fakeArticles)
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Success)

        // WHEN
        val errorMessage = fixture<String>()
        val errorState = UiState.Error(errorMessage)
        coEvery { repository.getTopHeadlines(any()) } returns errorState
        viewModel.loadHeadlines(isRefresh = false)
        advanceUntilIdle()

        // THEN
        assertEquals(errorState, viewModel.uiState.value)
    }

    @Test
    fun `retry after error state succeeds`() = runTest {
        // GIVEN
        val errorMessage = fixture<String>()
        val errorState = UiState.Error(errorMessage)
        coEvery { repository.getTopHeadlines(any()) } returns errorState
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        assertEquals(errorState, viewModel.uiState.value)

        // WHEN
        val fakeArticles = fixture<List<Article>>()
        val successState = UiState.Success(fakeArticles)
        coEvery { repository.getTopHeadlines(any()) } returns successState
        viewModel.retry()
        advanceUntilIdle()

        // THEN
        assertEquals(successState, viewModel.uiState.value)
        coVerify(exactly = 2) { repository.getTopHeadlines(any()) }
    }

    @Test
    fun `uiState starts with Loading before init completes`() = runTest {
        // GIVEN
        val fakeArticles = fixture<List<Article>>()
        coEvery { repository.getTopHeadlines(any()) } returns UiState.Success(fakeArticles)

        // WHEN
        viewModel = HeadlinesViewModel(repository)

        // THEN
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
