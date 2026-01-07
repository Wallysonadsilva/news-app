package com.newsapp.presentation.headlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState
import com.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()
    private val newsSource = "bbc-news"

    init {
        loadHeadlines()
    }

    fun loadHeadlines() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getTopHeadlines(newsSource)
            _uiState.value = result
        }
    }

    fun retry() {
        loadHeadlines()
    }
}
