package com.ark.arkpad

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.enums.enumEntries

fun alert(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

inline fun <reified T: Enum<T>> enumEntryAt(index: Int): T {
    return enumEntries<T>()
        .getOrNull(index)
        ?: throw IllegalArgumentException("index $index is out of bounds")
}

@Composable
fun <T> Flow<T?>.or(default: T): State<T> {
    return this.map { it ?: default }.collectAsState(default)
}