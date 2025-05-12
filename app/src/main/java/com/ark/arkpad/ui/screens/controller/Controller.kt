package com.ark.arkpad.ui.screens.controller

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Controller(
    brake: MutableFloatState,
    throttle: MutableFloatState,
    shiftUp: MutableState<Boolean>,
    shiftDown: MutableState<Boolean>,
) {
    Scaffold { innerPadding ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            AnalogTrigger(
                state = brake,
                envelope = AnalogEnvelope(
                    sustainMillis = 100,
                    releaseMillis = 400,
                    easing = FastOutSlowInEasing,
                ),
                color = Color.Red,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(2.5f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DigitalButton(state = shiftUp, icon = Icons.Default.Add)
                DigitalButton(state = shiftDown, icon = Icons.Default.Remove)
            }
            AnalogTrigger(
                state = throttle,
                envelope = AnalogEnvelope(
                    sustainMillis = 50,
                    releaseMillis = 200,
                    easing = LinearEasing,
                ),
                color = Color.Green,
                modifier = Modifier.weight(1f),
            )
        }
    }
}