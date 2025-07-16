package com.ark.arkpad.ui.screens.controller

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnalogTrigger(
    state: MutableFloatState,
    easing: Easing,
    sustainMillis: Long,
    releaseMillis: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    var bufferedInput by remember { mutableFloatStateOf(state.floatValue) }
    val smoothenedInput by animateFloatAsState(
        targetValue = bufferedInput,
        animationSpec = if (isDragged) {
            // removes any resistance while adjusting the slider
            snap()
        } else {
            tween(durationMillis = releaseMillis, easing = easing)
        }
    ).also {
        state.floatValue = it.value
    }

    Slider(
        value = smoothenedInput,
        onValueChange = { bufferedInput = it },
        onValueChangeFinished = {
            scope.launch {
                delay(sustainMillis)
                bufferedInput = 0f
            }
        },
        valueRange = 0f..255f,
        interactionSource = interactionSource,
        modifier = modifier.rotate(270f),
        colors = SliderDefaults.colors().copy(thumbColor = color, activeTrackColor = color),
    )
}

@Composable
fun LargeCircularIconButton(
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