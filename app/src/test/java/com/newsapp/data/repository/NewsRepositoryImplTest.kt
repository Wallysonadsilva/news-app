@file:Suppress("FunctionName")
package com.newsapp.data.repository

import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.data.remote.dto.NewsResponseDto
import com.newsapp.data.remote.dto.SourceDto
import com.newsapp.domain.model.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class NewsRepositoryImplTest {
    private lateinit var apiService: NewsApiService
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = NewsRepositoryImpl(apiService)
    }

    @Test
    fun `getTopHeadlines returns success with sorted articles when API call succeeds`() = runTest {
        // Given: Mock API response with 3 articles (different dates)
        val mockArticles = listOf(
            createMockArticleDto(
                title = "Article 1",
                publishedAt = "2024-01-05T10:00:00Z"
            ),
            createMockArticleDto(
                title = "Article 2",
                publishedAt = "2024-01-07T10:00:00Z"
            ),
            createMockArticleDto(
                title = "Article 3",
                publishedAt = "2024-01-06T10:00:00Z"
            )
        )

        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 3,
            articles = mockArticles
        )

        // When
        coEvery {
            apiService.getTopHeadlines(any(), any())
        } returns mockResponse

        // Then
        val result = repository.getTopHeadlines("bbc-news")

        // Verify
        assertTrue(result is UiState.Success)

        val articles = (result as UiState.Success).data

        assertEquals(3, articles.size)

        // Verify: Articles are sorted by date (newest first)
        assertEquals("Article 2", articles[0].title)  // 2024-01-07 (newest)
        assertEquals("Article 3", articles[1].title)  // 2024-01-06
        assertEquals("Article 1", articles[2].title)  // 2024-01-05 (oldest)
    }

    @Test
    fun `getTopHeadlines returns error when API call fails`() = runTest {
        // Given
        coEvery {
            apiService.getTopHeadlines(any(), any())
        } throws IOException("Network error")

        // When
        val result = repository.getTopHeadlines("bbc-news")

        // Then
        assertTrue(result is UiState.Error)

        val error = result as UiState.Error

        // Verify
        assertTrue(error.message.contains("Network error"))
    }

    @Test
    fun `getTopHeadlines returns success with empty list when API returns no articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 0,
            articles = emptyList()
        )

        coEvery {
            apiService.getTopHeadlines(any(), any())
        } returns mockResponse

        // When
        val result = repository.getTopHeadlines("bbc-news")

        // Then
        assertTrue(result is UiState.Success)

        val articles = (result as UiState.Success).data
        assertTrue(articles.isEmpty())
    }

    @Test
    fun `getTopHeadlines calls API with correct source parameter`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 0,
            articles = emptyList()
        )

        coEvery {
            apiService.getTopHeadlines("bbc-news", any())
        } returns mockResponse

        // When
        val result = repository.getTopHeadlines("bbc-news")

        // Then
        coVerify(exactly = 1) {
            apiService.getTopHeadlines("bbc-news", any())
        }

        assertTrue(result is UiState.Success)
    }

    private fun createMockArticleDto(
        title: String,
        publishedAt: String
    ): ArticleDto {
        return ArticleDto(
            source = SourceDto(id = "test", name = "Test Source"),
            author = "Test Author",
            title = title,
            description = "Test description",
            url = "https://test.com",
            urlToImage = "https://test.com/image.jpg",
            publishedAt = publishedAt,
            content = "Test content"
        )
    }
}
