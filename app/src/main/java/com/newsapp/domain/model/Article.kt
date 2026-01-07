package com.newsapp.domain.model

data class Article(
    val id: String,
    val source: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val content: String?
)
