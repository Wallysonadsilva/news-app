@file:Suppress("FunctionName")
package com.newsapp.data.repository

import com.appmattus.kotlinfixture.kotlinFixture
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
    private val fixture = kotlinFixture()
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
        val date1 = "2026-01-05T10:00:00Z"
        val date2 = "2026-01-07T10:00:00Z"
        val date3 = "2026-01-06T10:00:00Z"

        val mockArticles = listOf(
            fixture<ArticleDto>().copy(publishedAt = date1),
            fixture<ArticleDto>().copy(publishedAt = date2),
            fixture<ArticleDto>().copy(publishedAt = date3)
        )

        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = mockArticles.size,
            articles = mockArticles
        )

        // When
        coEvery {
            apiService.getTopHeadlines(any(), any())
        } returns mockResponse

        // Then
        val result = repository.getTopHeadlines("bbc-news")

        assertTrue(result is UiState.Success)
        val articles = (result as UiState.Success).data

        assertEquals(3, articles.size)
        // Verify
        assertEquals(date2, articles[0].publishedAt)
        assertEquals(date3, articles[1].publishedAt)
        assertEquals(date1, articles[2].publishedAt)
    }

    @Test
    fun `getTopHeadlines returns error when API call fails`() = runTest {
        // Given
        val errorMessage = fixture<String>()
        coEvery {
            apiService.getTopHeadlines(any(), any())
        } throws IOException(errorMessage)

        // When
        val result = repository.getTopHeadlines("bbc-news")

        // Then
        assertTrue(result is UiState.Error)

        val error = result as UiState.Error

        // Verify
        assertTrue(error.message.contains(errorMessage))
    }

    @Test
    fun `getTopHeadlines maps all DTO fields to domain correctly`() = runTest {
        // Given
        val testSourceName = fixture<String>()
        val testAuthor = fixture<String>()
        val testTitle = fixture<String>()
        val testDescription = fixture<String>()
        val testUrl = fixture<String>()
        val testImageUrl = fixture<String>()
        val testPublishedAt = "2026-01-15T10:30:00Z"
        val testContent = fixture<String>()

        val mockArticle = fixture<ArticleDto>().copy(
            source = SourceDto(id = fixture<String>(), name = testSourceName),
            author = testAuthor,
            title = testTitle,
            description = testDescription,
            url = testUrl,
            urlToImage = testImageUrl,
            publishedAt = testPublishedAt,
            content = testContent
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

        // Verify
        assertEquals(testSourceName, article.source)
        assertEquals(testAuthor, article.author)
        assertEquals(testTitle, article.title)
        assertEquals(testDescription, article.description)
        assertEquals(testUrl, article.url)
        assertEquals(testImageUrl, article.imageUrl)
        assertEquals(testPublishedAt, article.publishedAt)
        assertEquals(testContent, article.content)
        assertTrue(article.id.isNotEmpty())
    }
}
