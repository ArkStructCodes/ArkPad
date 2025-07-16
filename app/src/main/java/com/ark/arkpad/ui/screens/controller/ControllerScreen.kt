package com.ark.arkpad.ui.screens.controller

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ark.arkpad.alert
import com.ark.arkpad.preferences.PreferencesRepository
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// the screen rotation should be done from outside to ensure this does not recompose
@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel,
    preferences: PreferencesRepository,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val brake = remember { mutableFloatStateOf(0f) }
    val throttle = remember { mutableFloatStateOf(0f) }
    val shiftUp = remember { mutableStateOf(false) }
    val shiftDown = remember { mutableStateOf(false) }
    val handbrake = remember { mutableStateOf(false) }
    val accelerometer by rememberAccelerometerSensorValueAsState()

    LaunchedEffect(Unit) {
        while (controllerViewModel.isConnected) {
            controllerViewModel.transmit(
                shiftUp = shiftUp.value,
                shiftDown = shiftDown.value,
                handbrake = handbrake.value,
                brake = brake.floatValue,
                throttle = throttle.floatValue,
                tilt = accelerometer.value.second,
            )
            // delay each transmission in order to reduce CPU usage
            delay(20.milliseconds)
        }
    }

    // check every 5 seconds if the peer is still connected
    LaunchedEffect(Unit) {
        while (controllerViewModel.isConnected) {
            controllerViewModel.ping()
            delay(READ_TIMEOUT.milliseconds)
        }
        onNavigateBack()
    }

    // exit if any errors occur
    controllerViewModel.error.collectAsState().value?.let { error ->
        alert(context, error.message ?: "an unexpected error occurred")
        onNavigateBack()
    }

    BackHandler(onBack = onNavigateBack)

    Controller(shiftUp, shiftDown, handbrake, brake, throttle, preferences)
}