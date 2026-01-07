package com.newsapp.data.remote.api

import com.newsapp.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
    }
}
