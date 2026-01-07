package com.newsapp.presentation.headlines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState
import com.newsapp.presentation.headlines.ui.ArticleCard
import com.newsapp.presentation.headlines.ui.EmptyState
import com.newsapp.presentation.headlines.ui.ErrorState
import com.newsapp.presentation.headlines.ui.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadlinesScreen(
    viewModel: HeadlinesViewModel = hiltViewModel(),
    onArticleClick: (Article) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BBC News") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        // Handle different UI states
        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is UiState.Success -> {
                ArticlesList(
                    articles = state.data,
                    onArticleClick = onArticleClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ArticlesList(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    if (articles.isEmpty()) {
        EmptyState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = articles,
                key = { article -> article.id }
            ) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    }
}
