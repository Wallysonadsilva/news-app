package com.newsapp.domain.repository

import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState

interface NewsRepository {
    suspend fun getTopHeadlines(source: String): UiState<List<Article>>
}
