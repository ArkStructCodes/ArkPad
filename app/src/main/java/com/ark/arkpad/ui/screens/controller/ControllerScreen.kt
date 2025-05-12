package com.ark.arkpad.ui.screens.controller

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.ark.arkpad.alert
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// delay applied after each transmission in order to reduce CPU usage
const val TRANSMISSION_COOLDOWN = 20L

// the screen rotation should be done from outside to ensure this does not recompose
@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel,
    onNavigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    var brake = remember { mutableFloatStateOf(0f) }
    var throttle = remember { mutableFloatStateOf(0f) }
    var shiftUp = remember { mutableStateOf(false) }
    var shiftDown = remember { mutableStateOf(false) }
    val accelerometer by rememberAccelerometerSensorValueAsState()

    DisposableEffect(Unit) {
        controllerViewModel.connect()
        if (controllerViewModel.isConnected) {
            alert(context, "connected")
        }

        onDispose {
            controllerViewModel.disconnect()
        }
    }

    LaunchedEffect(Unit) {
        while (controllerViewModel.isConnected) {
            controllerViewModel.transmit(
                shiftUp = shiftUp.value,
                shiftDown = shiftDown.value,
                brake = brake.floatValue,
                throttle = throttle.floatValue,
                tilt = accelerometer.value.second,
            )
            delay(TRANSMISSION_COOLDOWN)
        }
    }

    // check every 5 seconds if the peer is still connected
    LaunchedEffect(Unit) {
        while (controllerViewModel.isConnected) {
            controllerViewModel.ping()
            delay(READ_TIMEOUT.toLong())
        }
        onNavigateToHome()
    }

    // exit if any errors occur
    controllerViewModel.error.collectAsState().also {
        if (it.value != null) {
            alert(context, it.value?.message ?: "an unexpected error occurred")
            onNavigateToHome()
        }
    }

    BackHandler(onBack = onNavigateToHome)

    Controller(brake, throttle, shiftUp, shiftDown)
}