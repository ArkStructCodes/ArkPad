package com.ark.arkpad.ui.screens.home

import android.net.InetAddresses
import android.os.Build
import android.util.Patterns
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ark.arkpad.model.Device
import com.ark.arkpad.ui.components.LimitedTextField
import com.ark.arkpad.ui.components.SimpleDialog

@Suppress("DEPRECATION")
private fun isHostValid(host: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        InetAddresses.isNumericAddress(host)
    } else {
        Patterns.IP_ADDRESS.matcher(host).matches()
    }
}

private fun isPortValid(port: String): Boolean {
    return try {
        port.toInt() in 0..65535
    } catch (_: NumberFormatException) {
        false
    }
}

@Composable
fun DeviceInputDialog(
    onConfirm: (device: Device) -> Unit,
    onDismiss: () -> Unit,
) {
    var name = remember { mutableStateOf("") }
    var host = remember { mutableStateOf("") }
    var port = remember { mutableStateOf("") }

    val isHostValid by remember {
        derivedStateOf { isHostValid(host.value) }
    }
    val isPortValid by remember {
        derivedStateOf { isPortValid(port.value) }
    }

    val (first, second, third, confirm) = remember { FocusRequester.createRefs() }

    SimpleDialog(
        title = "Add a New Device",
        onDismissRequest = onDismiss,
        confirmButton = {
            ElevatedButton(
                onClick = {
                    onConfirm(Device(name.value, host.value, port.value.toInt()))
                },
                enabled = name.value.isNotBlank() && isHostValid && isPortValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .focusRequester(confirm)
                    .focusable(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        Column {
            LimitedTextField(
                label = "Device Name",
                input = name,
                maxLength = 32,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { second.requestFocus() },
                ),
                modifier = Modifier.focusRequester(first),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                LimitedTextField(
                    label = "IP Address",
                    input = host,
                    maxLength = 15,
                    isValid = isHostValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { third.requestFocus() },
                    ),
                    modifier = Modifier
                        .weight(3f)
                        .focusRequester(second),
                )
                Spacer(modifier = Modifier.size(8.dp))
                LimitedTextField(
                    label = "Port",
                    input = port,
                    maxLength = 5,
                    isValid = isPortValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { confirm.requestFocus() },
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .focusRequester(third),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        first.requestFocus()
    }
}