package com.ark.arkpad.ui.components.multiselect

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ark.arkpad.ui.components.ScrollableLazyColumn

/**
 * Conditionally make items visible or transparent.
 *
 * @param condition the display condition
 * @param color an optional color to display instead of the current content color
 * @return color to display, transparent when [condition] is false
 */
@Composable
private fun visibleIf(condition: Boolean, color: Color = LocalContentColor.current): Color {
    return if (condition) color else Color.Transparent
}

/**
 * Selectable icon to be displayed alongside list items. In selection mode, this is left blank when
 * the item is not selected and a check mark when selected.
 *
 * @param isInSelectionMode whether selection mode is active
 * @param isSelected whether the item is currently selected
 * @param imageVector image to display when not in selection mode
 */
@Composable
private fun SelectableIcon(
    isInSelectionMode: Boolean,
    isSelected: Boolean,
    imageVector: ImageVector,
) {
    val fadingIconTint by animateColorAsState(visibleIf(isSelected))

    Crossfade(isInSelectionMode) {
        if (it) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (isSelected) "Selected" else "Not selected",
                tint = fadingIconTint,
            )
        } else {
            Icon(imageVector = imageVector, contentDescription = null)
        }
    }
}

/**
 * Selectable item for displaying information in a list. Highlights itself when selected.
 *
 * @param item object containing display information
 * @param isSelected whether the item is currently selected
 * @param modifier [Modifier] to be applied to the list item
 * @param leadingContent leading content for the list item, such as an icon
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectableListItem(
    item: ItemInfo,
    isSelected: Boolean,
    modifier: Modifier,
    leadingContent: @Composable () -> Unit,
) {
    val listItemColor by animateColorAsState(
        visibleIf(isSelected, MaterialTheme.colorScheme.secondaryContainer),
    )

    Surface(shape = MaterialTheme.shapes.large) {
        ListItem(
            modifier = modifier,
            colors = ListItemDefaults.colors(containerColor = listItemColor),
            leadingContent = leadingContent,
            headlineContent = { Text(text = item.name) },
            supportingContent = { Text(text = item.description) }
        )
    }
}

/**
 * Scrollable list that supports selection of multiple items at once.
 *
 * @param items list of items to display
 * @param selectedItems mutable list tracking the currently selected items
 * @param selectionState mutable state containing whether selection mode is active
 * @param onItemClick callback invoked for single clicking an item when not in selection mode
 * @param itemIcon icon to display alongside each item in selection mode
 * @param modifier [Modifier] to be applied to the list
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : ItemInfo> MultiSelectableList(
    items: List<T>,
    selectedItems: MutableList<T>,
    selectionState: MutableState<Boolean>,
    onItemClick: (T) -> Unit,
    itemIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    ScrollableLazyColumn(modifier) {
        items(items) { item ->
            val isSelected = selectedItems.contains(item)

            val handleItemClick = {
                if (selectionState.value) {
                    if (isSelected) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                    Unit
                } else {
                    onItemClick(item)
                }
            }

            val handleItemLongClick = {
                if (!selectionState.value) {
                    selectedItems.add(item)
                    selectionState.value = true
                }
            }

            SelectableListItem(
                item = item,
                isSelected = isSelected,
                modifier = Modifier.combinedClickable(
                    onClick = handleItemClick,
                    onLongClick = handleItemLongClick,
                ),
                leadingContent = {
                    SelectableIcon(
                        isInSelectionMode = selectionState.value,
                        isSelected = isSelected,
                        imageVector = itemIcon,
                    )
                },
            )
        }
    }
}