package com.newsapp.data.remote.dto

import com.newsapp.domain.model.Article
import java.util.UUID

fun ArticleDto.toDomain(): Article {
    return Article(
        id = generateArticleId(title, publishedAt),
        source = source.name,
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun List<ArticleDto>.toDomain(): List<Article> {
    return map { it.toDomain() }
}

private fun generateArticleId(title: String, publishedAt: String): String {
    return UUID.nameUUIDFromBytes("$title$publishedAt".toByteArray()).toString()
}
