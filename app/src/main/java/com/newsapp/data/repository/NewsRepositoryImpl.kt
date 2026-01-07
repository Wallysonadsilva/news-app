package com.newsapp.data.repository

import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.data.remote.dto.toDomain
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState
import com.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : NewsRepository {

    override suspend fun getTopHeadlines(source: String): UiState<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines(
                sources = source,
                apiKey = "API_KEY"
            )

            val articles = response.articles.toDomain()

            val sortedArticles = articles.sortedByDescending { it.publishedAt }

            UiState.Success(sortedArticles)

        } catch (e: Exception) {
            UiState.Error(
                message = "Failed to load headlines: ${e.message}",
                exception = e
            )
        }
    }
}
