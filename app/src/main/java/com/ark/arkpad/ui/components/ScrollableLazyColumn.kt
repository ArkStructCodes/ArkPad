package com.ark.arkpad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

/**
 * Wrapper around [LazyColumn] with scrolling configured using the automatically resizing scroll
 * thumb from [LazyColumnScrollbar].
 *
 * @param modifier [Modifier] to be applied to the [LazyColumn] within
 * @param listState optional object used to control or observe the list's state, the default value
 *   is persisted across compositions
 * @param content contents to display, with access to [LazyListScope]
 */
@Composable
fun ScrollableLazyColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}