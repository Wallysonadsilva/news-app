@file:Suppress("FunctionName")
package com.newsapp.data.repository

import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.data.remote.dto.NewsResponseDto
import com.newsapp.data.remote.dto.SourceDto
import com.newsapp.domain.model.UiState
import io.mockk.coEvery
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
        // Given
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
        assertEquals("Article 2", articles[0].title)
        assertEquals("Article 3", articles[1].title)
        assertEquals("Article 1", articles[2].title)
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
    fun `getTopHeadlines maps all DTO fields to domain correctly`() = runTest {
        // Given
        val mockArticle = ArticleDto(
            source = SourceDto(id = "test-id", name = "Test Source Name"),
            author = "John Doe",
            title = "Test Article Title",
            description = "Test article description",
            url = "https://example.com/article",
            urlToImage = "https://example.com/image.jpg",
            publishedAt = "2024-01-15T10:30:00Z",
            content = "Full article content here"
        )

        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(mockArticle)
        )

        coEvery {
            apiService.getTopHeadlines(any(), any())
        } returns mockResponse

        // When
        val result = repository.getTopHeadlines("bbc-news")

        // Then
        assertTrue(result is UiState.Success)
        val articles = (result as UiState.Success).data
        assertEquals(1, articles.size)

        val article = articles[0]

        // Verify all fields are mapped correctly
        assertEquals("Test Source Name", article.source)
        assertEquals("John Doe", article.author)
        assertEquals("Test Article Title", article.title)
        assertEquals("Test article description", article.description)
        assertEquals("https://example.com/article", article.url)
        assertEquals("https://example.com/image.jpg", article.imageUrl)
        assertEquals("2024-01-15T10:30:00Z", article.publishedAt)
        assertEquals("Full article content here", article.content)
        assertTrue(article.id.isNotEmpty())
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
