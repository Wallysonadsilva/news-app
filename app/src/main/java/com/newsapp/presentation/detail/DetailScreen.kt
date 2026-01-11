package com.newsapp.presentation.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.newsapp.navigation.Detail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detail: Detail,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showWebView by remember { mutableStateOf(false) }
    val webViewSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        }
    )

    Scaffold(
        topBar = {
            TopBar(
                title = detail.source,
                shareUrl = detail.url,
                context = context,
                onBack = onBack
            )
        },
        bottomBar = {
            Button(
                onClick = { showWebView = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Read Full Article")
            }
        }
    ) { paddingValues ->
        DetailContent(
            detail = detail,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        )
    }

    if (showWebView) {
        WebView(
            url = detail.url,
            title = detail.source,
            sheetState = webViewSheetState,
            onDismiss = { showWebView = false }
        )
    }
}
