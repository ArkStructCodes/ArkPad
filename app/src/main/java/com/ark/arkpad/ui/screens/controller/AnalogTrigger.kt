package com.ark.arkpad.ui.screens.controller

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Immutable
class AnalogEnvelope(
    val sustainMillis: Long,
    val releaseMillis: Int,
    val easing: Easing,
) {
    companion object {
        val Default = AnalogEnvelope(
            sustainMillis = 0,
            releaseMillis = 0,
            easing = LinearEasing,
        )
    }
}

@Composable
fun AnalogTrigger(
    state: MutableFloatState,
    modifier: Modifier = Modifier,
    envelope: AnalogEnvelope = AnalogEnvelope.Default,
    color: Color,
) {
    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    var bufferedInput by remember { mutableFloatStateOf(state.floatValue) }
    val smoothenedInput by animateFloatAsState(
        targetValue = bufferedInput,
        animationSpec = if (isDragged)
            // removes any resistance while adjusting the slider
            snap()
        else
            tween(durationMillis = envelope.releaseMillis, easing = envelope.easing)
    ).also {
        state.floatValue = it.value
    }

    Slider(
        value = smoothenedInput,
        onValueChange = { bufferedInput = it },
        onValueChangeFinished = {
            coroutineScope.launch {
                delay(envelope.sustainMillis)
                bufferedInput = 0f
            }
        },
        valueRange = 0f..255f,
        interactionSource = interactionSource,
        modifier = modifier.rotate(270f),
        colors = SliderDefaults.colors().copy(
            thumbColor = color,
            activeTrackColor = color,
        ),
    )
}