package com.ark.arkpad.ui.screens.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DigitalButton(
    state: MutableState<Boolean>,
    icon: ImageVector,
    size: Dp = 100.dp,
    contentPadding: Dp = 8.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    interactionSource
        .collectIsPressedAsState()
        .apply { state.value = this.value }

    Surface(
        onClick = {},
        modifier = Modifier.size(size),
        shape = CircleShape,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(contentPadding)
                .fillMaxSize(),
        )
    }
}