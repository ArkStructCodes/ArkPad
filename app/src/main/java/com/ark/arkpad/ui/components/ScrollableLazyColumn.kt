package com.ark.arkpad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun ScrollableLazyColumn(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumnScrollbar(
        state = listState,
        settings = ScrollbarSettings(
            thumbUnselectedColor = MaterialTheme.colorScheme.primaryContainer,
            thumbSelectedColor = MaterialTheme.colorScheme.primary,
            scrollbarPadding = 4.dp,
            thumbThickness = 8.dp,
        ),
    ) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}