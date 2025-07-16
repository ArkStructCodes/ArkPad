package com.ark.arkpad.preferences

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.ark.arkpad.enumEntryAt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class EasingType {
    FAST_OUT_SLOW_IN,
    LINEAR_OUT_SLOW_IN,
    FAST_OUT_LINEAR_IN,
    LINEAR;

    fun getEasingFunction(): Easing {
        return when (this) {
            FAST_OUT_SLOW_IN -> FastOutSlowInEasing
            LINEAR_OUT_SLOW_IN -> LinearOutSlowInEasing
            FAST_OUT_LINEAR_IN -> FastOutLinearInEasing
            LINEAR -> LinearEasing
        }
    }

    companion object {
        fun entryAt(index: Int): EasingType {
            return enumEntryAt(index)
        }
    }
}

enum class KeyCode(val offset: Int) {
    TL(0),
    TR(1),
    SOUTH(4),
    EAST(5),
    WEST(6),
    NORTH(7);

    companion object {
        fun entryAt(index: Int): KeyCode {
            return enumEntryAt(index)
        }
    }
}

class PreferencesRepository(private val datastore: DataStore<Preferences>) {
    private companion object {
        val AUTO_CLUTCH = booleanPreferencesKey("auto_clutch")
        val SHIFT_UP_KEYCODE = intPreferencesKey("shift_up_keycode")
        val SHIFT_DOWN_KEYCODE = intPreferencesKey("shift_down_keycode")
        val CLUTCH_KEYCODE = intPreferencesKey("clutch_keycode")
        val HANDBRAKE_KEYCODE = intPreferencesKey("handbrake_keycode")
        val BRAKE_EASING = intPreferencesKey("brake_easing")
        val BRAKE_SUSTAIN_MS = intPreferencesKey("brake_sustain_ms")
        val BRAKE_RELEASE_MS = intPreferencesKey("brake_release_ms")
        val THROTTLE_EASING = intPreferencesKey("throttle_easing")
        val THROTTLE_SUSTAIN_MS = intPreferencesKey("throttle_sustain_ms")
        val THROTTLE_RELEASE_MS = intPreferencesKey("throttle_release_ms")
    }

    private fun <T> get(key: Preferences.Key<T>): Flow<T?> {
        return datastore.data.map { it[key] }
    }

    private suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        datastore.edit { it[key] = value }
    }

    fun getShiftUpKeyCode(): Flow<KeyCode?> {
        return get(SHIFT_UP_KEYCODE).map { value ->
            value?.let { KeyCode.entryAt(it) }
        }
    }

    suspend fun setShiftUpKeyCode(keyCode: KeyCode) {
        set(SHIFT_UP_KEYCODE, keyCode.ordinal)
    }

    fun getShiftDownKeyCode(): Flow<KeyCode?> {
        return get(SHIFT_DOWN_KEYCODE).map { value ->
            value?.let { KeyCode.entryAt(it) }
        }
    }

    suspend fun setShiftDownKeyCode(keyCode: KeyCode) {
        set(SHIFT_DOWN_KEYCODE, keyCode.ordinal)
    }

    fun getClutchKeyCode(): Flow<KeyCode?> {
        return get(CLUTCH_KEYCODE).map { value ->
            value?.let { KeyCode.entryAt(it) }
        }
    }

    suspend fun setClutchKeyCode(keyCode: KeyCode) {
        set(CLUTCH_KEYCODE, keyCode.ordinal)
    }

    fun getHandbrakeKeyCode(): Flow<KeyCode?> {
        return get(HANDBRAKE_KEYCODE).map { value ->
            value?.let { KeyCode.entryAt(it) }
        }
    }

    suspend fun setHandbrakeKeyCode(keyCode: KeyCode) {
        set(HANDBRAKE_KEYCODE, keyCode.ordinal)
    }

    fun getAutoClutch(): Flow<Boolean?> {
        return get(AUTO_CLUTCH)
    }

    suspend fun setAutoClutch(state: Boolean) {
        set(AUTO_CLUTCH, state)
    }

    fun getBrakeEasing(): Flow<EasingType?> {
        return get(BRAKE_EASING).map { value ->
            value?.let { EasingType.entryAt(it) }
        }
    }

    suspend fun setBrakeEasing(easingType: EasingType) {
        set(BRAKE_EASING, easingType.ordinal)
    }

    fun getBrakeSustain(): Flow<Int?> {
        return get(BRAKE_SUSTAIN_MS)
    }

    suspend fun setBrakeSustain(millis: Int) {
        set(BRAKE_SUSTAIN_MS, millis)
    }

    fun getBrakeRelease(): Flow<Int?> {
        return get(BRAKE_RELEASE_MS)
    }

    suspend fun setBrakeRelease(millis: Int) {
        set(BRAKE_RELEASE_MS, millis)
    }

    fun getThrottleEasing(): Flow<EasingType?> {
        return get(THROTTLE_EASING).map { value ->
            value?.let { EasingType.entryAt(it) }
        }
    }

    suspend fun setThrottleEasing(easingType: EasingType) {
        set(THROTTLE_EASING, easingType.ordinal)
    }

    fun getThrottleSustain(): Flow<Int?> {
        return get(THROTTLE_SUSTAIN_MS)
    }

    suspend fun setThrottleSustain(millis: Int) {
        set(THROTTLE_SUSTAIN_MS, millis)
    }

    fun getThrottleRelease(): Flow<Int?> {
        return get(THROTTLE_RELEASE_MS)
    }

    suspend fun setThrottleRelease(millis: Int) {
        set(THROTTLE_RELEASE_MS, millis)
    }
}