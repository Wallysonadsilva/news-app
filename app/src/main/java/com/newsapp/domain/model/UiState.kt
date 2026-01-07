package com.newsapp.domain.model

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message: String,
        val exception: Exception? = null
    ) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}
