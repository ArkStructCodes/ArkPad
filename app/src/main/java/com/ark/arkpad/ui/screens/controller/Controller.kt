package com.ark.arkpad.ui.screens.controller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ark.arkpad.or
import com.ark.arkpad.preferences.DEFAULT_BRAKE_EASING
import com.ark.arkpad.preferences.DEFAULT_BRAKE_RELEASE_MS
import com.ark.arkpad.preferences.DEFAULT_BRAKE_SUSTAIN_MS
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_EASING
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_RELEASE_MS
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_SUSTAIN_MS
import com.ark.arkpad.preferences.PreferencesRepository

@Composable
fun Controller(
    shiftUp: MutableState<Boolean>,
    shiftDown: MutableState<Boolean>,
    handbrake: MutableState<Boolean>,
    brake: MutableFloatState,
    throttle: MutableFloatState,
    preferences: PreferencesRepository,
) {
    val brakeEasing by preferences.getBrakeEasing().or(DEFAULT_BRAKE_EASING)
    val brakeSustain by preferences.getBrakeSustain().or(DEFAULT_BRAKE_SUSTAIN_MS)
    val brakeRelease by preferences.getBrakeRelease().or(DEFAULT_BRAKE_RELEASE_MS)

    val throttleEasing by preferences.getThrottleEasing().or(DEFAULT_THROTTLE_EASING)
    val throttleSustain by preferences.getThrottleSustain().or(DEFAULT_THROTTLE_SUSTAIN_MS)
    val throttleRelease by preferences.getThrottleRelease().or(DEFAULT_THROTTLE_RELEASE_MS)

    Scaffold { innerPadding ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            AnalogTrigger(
                state = brake,
                easing = brakeEasing.getEasingFunction(),
                sustainMillis = brakeSustain.toLong(),
                releaseMillis = brakeRelease,
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
                LargeCircularIconButton(state = shiftUp, icon = Icons.Default.Add)
                LargeCircularIconButton(state = handbrake, icon = Icons.Default.WarningAmber)
                LargeCircularIconButton(state = shiftDown, icon = Icons.Default.Remove)
            }
            AnalogTrigger(
                state = throttle,
                easing = throttleEasing.getEasingFunction(),
                sustainMillis = throttleSustain.toLong(),
                releaseMillis = throttleRelease,
                color = Color.Green,
                modifier = Modifier.weight(1f),
            )
        }
    }
}