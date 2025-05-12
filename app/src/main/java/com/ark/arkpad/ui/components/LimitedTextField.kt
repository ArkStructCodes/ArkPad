package com.ark.arkpad.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LimitedTextField(
    input: MutableState<String>,
    maxLength: Int,
    label: String,
    modifier: Modifier = Modifier,
    isValid: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = input.value,
        onValueChange = { input.value = it.take(maxLength) },
        // setting the height allows us to prevent layout shifts caused by the animated label
        modifier = modifier.height(96.dp),
        // increasing this is required due to the height increase from default
        textStyle = MaterialTheme.typography.bodyLarge,
        label = { Text(label) },
        supportingText = { Text("${input.value.length} / $maxLength") },
        // we do not want to indicate erroneous input when there is nothing being input
        isError = input.value.isNotEmpty() && !isValid,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
    )
}