package com.ark.arkpad.ui.components.multiselect

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.ark.arkpad.ui.components.FadeIn

/**
 * Top app bar to be displayed in selection mode. Contains a button to cancel selection.
 *
 *  @param selectedItems list of currently selected items, its size being shown in the title
 *  @param onSelectionExit callback invoked when the exit button is clicked
 *  @param actions actions displayed at the end of the top app bar
 *  @param colors a [TopAppBarColors] that will be used to resolve the colors used for this
 *    top app bar in different states
 *  @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will
 *    be applied by this top app bar to set up its height and colors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T: ItemInfo> SelectionModeTopAppBar(
    selectedItems: List<T>,
    onSelectionExit: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeTopAppBar(
        title = { Text("${selectedItems.size} selected") },
        navigationIcon = {
            FadeIn {
                IconButton(onClick = onSelectionExit) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit selection mode.",
                    )
                }
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Screen layout that supports both normal and multi selection modes.
 *
 * In normal mode, a top app bar is displayed with the given title. In selection mode, the title
 * instead indicates how many items are selected and provides an exit action. Back navigation exits
 * selection mode when active.
 *
 * @param title title to show in the normal mode of the top app bar
 * @param selectionState whether the screen is currently in selection mode
 * @param onSelectionExit callback invoked to exit selection mode
 * @param selectedItems list of currently selected items, its size being shown in the title
 * @param floatingActionButton optional main action button of the screen
 * @param actions optional row of actions displayed at the end of the top app bar
 * @param content content of the screen, receives a [PaddingValues] that should be applied to the
 *   content root
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: ItemInfo> MultiSelectableScreen(
    title: String,
    selectionState: State<Boolean>,
    onSelectionExit: () -> Unit,
    selectedItems: MutableList<T>,
    floatingActionButton: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val topAppBarColors = TopAppBarDefaults.largeTopAppBarColors(
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (selectionState.value) {
                SelectionModeTopAppBar(
                    selectedItems = selectedItems,
                    onSelectionExit = onSelectionExit,
                    actions = actions,
                    colors = topAppBarColors,
                    scrollBehavior = scrollBehavior,
                )
            } else {
                LargeTopAppBar(
                    title = { Text(title) },
                    actions = actions,
                    colors = topAppBarColors,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        floatingActionButton = floatingActionButton,
    ) { innerPadding ->
        content(innerPadding)
    }

    BackHandler(enabled = selectionState.value, onBack = onSelectionExit)
}