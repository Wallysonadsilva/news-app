package com.newsapp.presentation.headlines

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

    private lateinit var repository: NewsRepository
    private lateinit var viewModel: HeadlinesViewModel

    private val fakeArticles = listOf(
        Article(
            id = "1",
            source = "bbc-news",
            author = "Test Author",
            title = "Test title",
            description = "Test description",
            url = "https://test.com",
            imageUrl = "https://test.com/image.jpg",
            publishedAt = "2025-01-01T00:00:00Z",
            content = "Test content"
        )
    )

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
        val errorState = UiState.Error("Network error")
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
        coEvery { repository.getTopHeadlines(any()) } returns UiState.Success(fakeArticles)
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Success)

        // WHEN
        val errorState = UiState.Error("Network connection lost")
        coEvery { repository.getTopHeadlines(any()) } returns errorState
        viewModel.loadHeadlines(isRefresh = false)
        advanceUntilIdle()

        // THEN
        assertEquals(errorState, viewModel.uiState.value)
    }

    @Test
    fun `retry after error state succeeds`() = runTest {
        // GIVEN
        val errorState = UiState.Error("Initial network error")
        coEvery { repository.getTopHeadlines(any()) } returns errorState
        viewModel = HeadlinesViewModel(repository)
        advanceUntilIdle()

        assertEquals(errorState, viewModel.uiState.value)

        // WHEN
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
        coEvery { repository.getTopHeadlines(any()) } returns UiState.Success(fakeArticles)

        // WHEN
        viewModel = HeadlinesViewModel(repository)

        // THEN
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
