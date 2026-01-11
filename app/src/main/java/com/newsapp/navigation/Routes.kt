package com.newsapp.navigation

import kotlinx.serialization.Serializable

@Serializable
object Headlines

@Serializable
data class Detail(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val content: String?,
)
