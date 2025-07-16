package com.ark.arkpad.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.ark.arkpad.or
import com.ark.arkpad.preferences.DEFAULT_AUTO_CLUTCH
import com.ark.arkpad.preferences.DEFAULT_BRAKE_EASING
import com.ark.arkpad.preferences.DEFAULT_BRAKE_RELEASE_MS
import com.ark.arkpad.preferences.DEFAULT_BRAKE_SUSTAIN_MS
import com.ark.arkpad.preferences.DEFAULT_CLUTCH_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_HANDBRAKE_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_SHIFT_DOWN_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_SHIFT_UP_KEYCODE
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_EASING
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_RELEASE_MS
import com.ark.arkpad.preferences.DEFAULT_THROTTLE_SUSTAIN_MS
import com.ark.arkpad.preferences.EasingType
import com.ark.arkpad.preferences.KeyCode
import com.ark.arkpad.preferences.PreferencesRepository
import com.ark.arkpad.ui.components.RadioGroup
import com.ark.arkpad.ui.components.dialog.BasicDialog
import com.ark.arkpad.ui.components.dialog.PromptDialog
import kotlinx.coroutines.launch

@Composable
private fun Section(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
private fun Toggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = { onCheckedChange(!checked) }),
        headlineContent = { Text(label) },
        supportingContent = { Text(description) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Composable
fun OptionSelector(
    label: String,
    options: List<String>,
    selectedOption: Int,
    onSelect: (Int) -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    if (isDialogVisible) {
        BasicDialog(
            title = label,
            onDismissRequest = { isDialogVisible = false },
        ) {
            RadioGroup(
                options = options,
                selectedIndex = selectedOption,
                onSelect = { index ->
                    onSelect(index)
                    isDialogVisible = false
                },
            )
        }
    }

    ListItem(
        modifier = Modifier.clickable(onClick = { isDialogVisible = true }),
        headlineContent = { Text(label) },
        supportingContent = { Text(options[selectedOption]) },
    )
}

@Composable
fun DurationInput(
    label: String,
    initialValue: Int,
    onSubmit: (Int) -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(initialValue.toFloat()) }

    if (isDialogVisible) {
        PromptDialog(
            title = label,
            onDismissRequest = { isDialogVisible = false },
            dismissButton = {
                TextButton(onClick = { isDialogVisible = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSubmit(sliderPosition.toInt())
                    isDialogVisible = false
                }) {
                    Text("OK")
                }
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it.fastRoundToInt().toFloat() },
                    valueRange = 0f..1000f,
                )
                Text("${sliderPosition.toInt()} ms")
            }
        }
    }

    ListItem(
        modifier = Modifier.clickable(onClick = { isDialogVisible = true }),
        headlineContent = { Text(label) },
        supportingContent = { Text("$initialValue ms") },
    )
}

@Composable
fun Settings(preferences: PreferencesRepository) {
    val scope = rememberCoroutineScope()

    val autoClutch by preferences.getAutoClutch().or(DEFAULT_AUTO_CLUTCH)
    val shiftUpKeyCode by preferences.getShiftUpKeyCode().or(DEFAULT_SHIFT_UP_KEYCODE)
    val shiftDownKeyCode by preferences.getShiftDownKeyCode().or(DEFAULT_SHIFT_DOWN_KEYCODE)
    val clutchKeyCode by preferences.getClutchKeyCode().or(DEFAULT_CLUTCH_KEYCODE)
    val handbrakeKeyCode by preferences.getHandbrakeKeyCode().or(DEFAULT_HANDBRAKE_KEYCODE)
    val brakeEasingType by preferences.getBrakeEasing().or(DEFAULT_BRAKE_EASING)
    val brakeSustain by preferences.getBrakeSustain().or(DEFAULT_BRAKE_SUSTAIN_MS)
    val brakeRelease by preferences.getBrakeRelease().or(DEFAULT_BRAKE_RELEASE_MS)
    val throttleEasingType by preferences.getThrottleEasing().or(DEFAULT_THROTTLE_EASING)
    val throttleSustain by preferences.getThrottleSustain().or(DEFAULT_THROTTLE_SUSTAIN_MS)
    val throttleRelease by preferences.getThrottleRelease().or(DEFAULT_THROTTLE_RELEASE_MS)

    val keyCodes = remember { KeyCode.entries.map { it.name } }
    val easingTypes = remember { EasingType.entries.map { it.name.replace("_", " ") } }

    Toggle(
        label = "Enable automatic clutch",
        description = "Simulates a clutch press while shifting",
        checked = autoClutch,
        onCheckedChange = {
            scope.launch {
                preferences.setAutoClutch(it)
            }
        },
    )

    Section("Button mapping")

    OptionSelector(
        label = "Shift up keycode",
        selectedOption = shiftUpKeyCode.ordinal,
        options = keyCodes,
        onSelect = {
            scope.launch {
                preferences.setShiftUpKeyCode(KeyCode.entryAt(it))
            }
        },
    )

    OptionSelector(
        label = "Shift down keycode",
        selectedOption = shiftDownKeyCode.ordinal,
        options = keyCodes,
        onSelect = {
            scope.launch {
                preferences.setShiftDownKeyCode(KeyCode.entryAt(it))
            }
        },
    )

    OptionSelector(
        label = "Clutch keycode",
        selectedOption = clutchKeyCode.ordinal,
        options = keyCodes,
        onSelect = {
            scope.launch {
                preferences.setClutchKeyCode(KeyCode.entryAt(it))
            }
        },
    )

    OptionSelector(
        label = "Handbrake keycode",
        selectedOption = handbrakeKeyCode.ordinal,
        options = keyCodes,
        onSelect = {
            scope.launch {
                preferences.setHandbrakeKeyCode(KeyCode.entryAt(it))
            }
        },
    )

    Section("Brake")

    OptionSelector(
        label = "Brake easing type",
        selectedOption = brakeEasingType.ordinal,
        options = easingTypes,
        onSelect = {
            scope.launch {
                preferences.setBrakeEasing(EasingType.entryAt(it))
            }
        },
    )

    DurationInput(
        label = "Brake sustain duration",
        initialValue = brakeSustain,
        onSubmit = {
            scope.launch {
                preferences.setBrakeSustain(it)
            }
        },
    )

    DurationInput(
        label = "Brake release duration",
        initialValue = brakeRelease,
        onSubmit = {
            scope.launch {
                preferences.setBrakeRelease(it)
            }
        },
    )

    Section("Throttle")

    OptionSelector(
        label = "Throttle easing type",
        selectedOption = throttleEasingType.ordinal,
        options = easingTypes,
        onSelect = {
            scope.launch {
                preferences.setThrottleEasing(EasingType.entryAt(it))
            }
        },
    )

    DurationInput(
        label = "Throttle sustain duration",
        initialValue = throttleSustain,
        onSubmit = {
            scope.launch {
                preferences.setThrottleSustain(it)
            }
        },
    )

    DurationInput(
        label = "Throttle release duration",
        initialValue = throttleRelease,
        onSubmit = {
            scope.launch {
                preferences.setThrottleRelease(it)
            }
        },
    )
}