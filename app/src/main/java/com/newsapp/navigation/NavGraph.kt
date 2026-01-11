package com.newsapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.newsapp.presentation.detail.DetailScreen
import com.newsapp.presentation.headlines.HeadlinesScreen

@Composable
fun NewsNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Headlines
    ) {
        // Headlines list screen
        composable<Headlines> {
            HeadlinesScreen(
                onArticleClick = { article ->
                    navController.navigate(
                        Detail(
                            id = article.id,
                            title = article.title,
                            description = article.description,
                            url = article.url,
                            imageUrl = article.imageUrl,
                            source = article.source,
                            content = article.content
                        )
                    )
                }
            )
        }

        // Article detail screen
        composable<Detail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Detail>()
            DetailScreen(
                detail = detail,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
