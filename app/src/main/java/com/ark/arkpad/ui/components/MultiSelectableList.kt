package com.ark.arkpad.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

/**
 * Conditionally display content.
 * @param condition The display condition.
 * @param color An optional color to display instead of the current content color.
 * @return Color to display. Transparent when [condition] is false.
 */
@Composable
private fun visibleIf(condition: Boolean, color: Color = LocalContentColor.current): Color {
    return if (condition) color else Color.Transparent
}

@Composable
private fun SelectableIcon(
    imageVector: ImageVector,
    isInSelectionMode: Boolean,
    isSelected: Boolean,
) {
    val fadableTint by animateColorAsState(visibleIf(isSelected))

    Crossfade(isInSelectionMode) {
        if (it) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (isSelected) "Selected" else "Not selected",
                tint = fadableTint,
            )
        } else {
            Icon(imageVector = imageVector, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectableListItem(
    item: DisplayItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val listItemColor by animateColorAsState(
        visibleIf(isSelected, MaterialTheme.colorScheme.secondaryContainer),
    )

    Surface(shape = MaterialTheme.shapes.large) {
        ListItem(
            modifier = Modifier.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
            colors = ListItemDefaults.colors(containerColor = listItemColor),
            leadingContent = icon,
            headlineContent = { Text(text = item.name, fontSize = 20.sp) },
            supportingContent = { Text(text = item.description) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : DisplayItem> MultiSelectableList(
    items: List<T>,
    onItemClick: (T) -> Unit,
    selectedItems: MutableList<T>,
    itemIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val isInSelectionMode = selectedItems.isNotEmpty()

    ScrollableLazyColumn(modifier) {
        items(items) { item ->
            val isSelected = selectedItems.contains(item)

            SelectableListItem(
                item = item,
                isSelected = isSelected,
                onClick = {
                    if (isInSelectionMode) {
                        if (isSelected) {
                            selectedItems.remove(item)
                        } else {
                            selectedItems.add(item)
                        }
                    } else {
                        onItemClick(item)
                    }
                },
                onLongClick = {
                    if (!isInSelectionMode) {
                        selectedItems.add(item)
                    }
                },
                icon = { SelectableIcon(itemIcon, isInSelectionMode, isSelected) },
            )
        }
    }
}