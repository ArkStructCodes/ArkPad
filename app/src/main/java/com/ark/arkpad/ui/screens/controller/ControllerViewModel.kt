package com.ark.arkpad.ui.screens.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.arkpad.model.Device
import com.ark.arkpad.preferences.DEFAULT_AUTO_CLUTCH
import com.ark.arkpad.preferences.DEFAULT_CLUTCH_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_HANDBRAKE_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_SHIFT_DOWN_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_SHIFT_UP_KEYCODE
import com.ark.arkpad.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ControllerViewModel(
    private val device: Device,
    private val preferences: PreferencesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val client = NetworkClient(device.host, device.port)
    val isConnected: Boolean
        get() = client.isConnected

    private val _error = MutableStateFlow<Exception?>(null)
    val error = _error.asStateFlow()

    private var autoClutch = DEFAULT_AUTO_CLUTCH
    private var shiftUpOffset = DEFAULT_SHIFT_UP_KEYCODE.offset
    private var shiftDownOffset = DEFAULT_SHIFT_DOWN_KEYCODE.offset
    private var clutchOffset = DEFAULT_CLUTCH_KEYCODE.offset
    private var handbrakeOffset = DEFAULT_HANDBRAKE_KEYCODE.offset

    init {
        tryLaunch {
            preferences.getAutoClutch().first()?.let { autoClutch = it }
            preferences.getClutchKeyCode().first()?.let { clutchOffset = it.offset }
            preferences.getShiftUpKeyCode().first()?.let { shiftUpOffset = it.offset }
            preferences.getShiftDownKeyCode().first()?.let { shiftDownOffset = it.offset }
            preferences.getHandbrakeKeyCode().first()?.let { handbrakeOffset = it.offset }
        }
        Log.d("Controller", "connecting to ${device.description}")
        connect()
    }

    /** Launch a coroutine using the IO dispatcher and exit silently on errors. */
    private fun tryLaunch(
        scope: CoroutineScope = viewModelScope,
        function: suspend CoroutineScope.() -> Unit,
    ) {
        scope.launch(dispatcher) {
            try {
                function()
            } catch (e: Exception) {
                ensureActive()
                _error.value = e
            }
        }
    }

    private fun connect() {
        tryLaunch { client.connect() }
    }

    private fun disconnect() {
        tryLaunch(CoroutineScope(SupervisorJob() + dispatcher)) {
            withContext(NonCancellable) {
                client.disconnect()
            }
        }
    }

    fun transmit(
        shiftUp: Boolean,
        shiftDown: Boolean,
        handbrake: Boolean,
        brake: Float,
        throttle: Float,
        tilt: Float,
    ) {
        val scaledTilt = (tilt.coerceIn(-9.8f, 9.8f) / 9.8f * 32767.0f).toInt()
        var buttons = 0

        if (shiftUp) {
            if (autoClutch) {
                buttons = buttons or (1 shl clutchOffset)
            }
            buttons = buttons or (1 shl shiftUpOffset)
        }

        if (shiftDown) {
            if (autoClutch) {
                buttons = buttons or (1 shl clutchOffset)
            }
            buttons = buttons or (1 shl shiftDownOffset)
        }

        if (handbrake) {
            buttons = buttons or (1 shl handbrakeOffset)
        }

        tryLaunch {
            client.emit(
                byteArrayOf(
                    0, buttons.toByte(),
                    brake.toInt().toByte(), throttle.toInt().toByte(),
                    (scaledTilt shr 8).toByte(), scaledTilt.toByte(),
                    0, 0,
                    0, 0,
                    0, 0,
                )
            )
        }
    }

    fun ping() {
        tryLaunch { client.ping() }
    }

    override fun onCleared() {
        Log.d("Controller", "disconnecting from ${device.description}")
        disconnect()
    }
}