package com.newsapp.presentation.headlines

import androidx.compose.foundation.background
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.UiState
import com.newsapp.presentation.headlines.ui.ArticleCard
import com.newsapp.presentation.headlines.ui.EmptyState
import com.newsapp.presentation.headlines.ui.ErrorState
import com.newsapp.presentation.headlines.ui.LoadingState
import com.newsapp.ui.theme.NewsAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadlinesScreen(
    viewModel: HeadlinesViewModel = hiltViewModel(),
    onArticleClick: (Article) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HeadlinesContent(
        uiState = uiState,
        onRefresh = { viewModel.loadHeadlines() },
        onRetry = { viewModel.retry() },
        onArticleClick = onArticleClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeadlinesContent(
    uiState: UiState<List<Article>>,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onArticleClick: (Article) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BBC News") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F9AAE),
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is UiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is UiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = onRefresh
                ) {
                    ArticlesList(
                        articles = uiState.data,
                        onArticleClick = onArticleClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is UiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = onRetry,
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
                key = { it.id }
            ) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeadlinesSuccessPreview() {
    NewsAppTheme {
        HeadlinesContent(
            uiState = UiState.Success(sampleArticles),
            onRefresh = {},
            onRetry = {},
            onArticleClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeadlinesErrorPreview() {
    NewsAppTheme {
        HeadlinesContent(
            uiState = UiState.Error("Failed to load headlines"),
            onRefresh = {},
            onRetry = {},
            onArticleClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeadlinesEmptyPreview() {
    NewsAppTheme {
        HeadlinesContent(
            uiState = UiState.Success(emptyList()),
            onRefresh = {},
            onRetry = {},
            onArticleClick = {}
        )
    }
}

private val sampleArticles = listOf(
    Article(
        id = "1",
        source = "News Source",
        author = "Author Name",
        title = "Article Title Goes Here",
        description = "Article description with some details about the content.",
        url = "https://example.com",
        imageUrl = "https://via.placeholder.com/400x200",
        publishedAt = "",
        content = null
    ),
    Article(
        id = "2",
        source = "News Source",
        author = "Author Name",
        title = "Another Article Title",
        description = "Article description with some details about the content.",
        url = "https://example.com",
        imageUrl = "https://via.placeholder.com/400x200",
        publishedAt = "",
        content = null
    )
)
