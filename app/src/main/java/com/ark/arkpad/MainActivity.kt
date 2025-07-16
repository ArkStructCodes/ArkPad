package com.ark.arkpad

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.preferencesDataStore
import com.ark.arkpad.preferences.PreferencesRepository
import com.ark.arkpad.ui.theme.ArkPadTheme

private val Context.dataStore by preferencesDataStore("preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = PreferencesRepository(this.dataStore)
        setContent {
            ArkPadTheme { ArkPadNavHost(preferences) }
        }
    }
}