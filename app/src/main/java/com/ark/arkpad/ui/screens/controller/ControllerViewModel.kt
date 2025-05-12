package com.ark.arkpad.ui.screens.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.arkpad.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private fun encodeButtons(shiftUp: Boolean, shiftDown: Boolean): Byte {
    // automatically apply clutch to shift faster
    return if (shiftUp) {
        (1 or (1 shl 5)).toByte()
    } else if (shiftDown) {
        (1 or (1 shl 7)).toByte()
    } else {
        0
    }
}

private fun encodeInputData(
    shiftUp: Boolean,
    shiftDown: Boolean,
    brake: Float,
    throttle: Float,
    tilt: Float,
): ByteArray {
    val scaledTilt = (tilt.coerceIn(-9.8f, 9.8f) / 9.8f * 32767.0f).toInt()
    return byteArrayOf(
        0, encodeButtons(shiftUp, shiftDown),
        brake.toInt().toByte(), throttle.toInt().toByte(),
        (scaledTilt shr 8).toByte(), scaledTilt.toByte(),
        0, 0,
        0, 0,
        0, 0,
    )
}

class ControllerViewModel(device: Device) : ViewModel() {
    private val client = NetworkClient(device.host, device.port)
    val isConnected: Boolean
        get() = client.isConnected

    private val _error = MutableStateFlow<Exception?>(null)
    val error = _error.asStateFlow()

    private fun tryLaunch(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun connect() {
        tryLaunch { client.connect() }
    }

    fun transmit(
        shiftUp: Boolean,
        shiftDown: Boolean,
        brake: Float,
        throttle: Float,
        tilt: Float,
    ) {
        tryLaunch {
            client.emit(encodeInputData(shiftUp, shiftDown, brake, throttle, tilt))
        }
    }

    fun ping() {
        tryLaunch { client.ping() }
    }

    fun disconnect() {
        tryLaunch { client.disconnect() }
    }
}