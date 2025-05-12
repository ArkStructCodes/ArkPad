package com.ark.arkpad.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : DisplayItem> MultiSelectableScreen(
    selectedItems: MutableList<T>,
    title: String,
    floatingActionButton: @Composable () -> Unit = {},
    selectionModeActions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val isInSelectionMode = selectedItems.isNotEmpty()
    val exitSelectionMode = {
        selectedItems.clear()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Crossfade(isInSelectionMode) {
                if (it) {
                    SelectionModeTopAppBar(
                        selectedItems = selectedItems,
                        onSelectionExit = exitSelectionMode,
                        actions = selectionModeActions,
                        scrollBehavior = scrollBehavior,
                    )
                } else {
                    LargeTopAppBar(
                        title = { Text(title) },
                        scrollBehavior = scrollBehavior,
                    )
                }
            }
        },
        floatingActionButton = floatingActionButton,
    ) { innerPadding ->
        content(innerPadding)
    }

    BackHandler(enabled = isInSelectionMode, onBack = exitSelectionMode)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : DisplayItem> SelectionModeTopAppBar(
    selectedItems: List<T>,
    onSelectionExit: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeTopAppBar(
        title = { Text("${selectedItems.size} selected") },
        navigationIcon = {
            IconButton(onClick = onSelectionExit) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear all selections",
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        scrollBehavior = scrollBehavior,
    )
}