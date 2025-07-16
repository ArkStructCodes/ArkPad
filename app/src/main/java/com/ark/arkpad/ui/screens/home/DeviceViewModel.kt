package com.ark.arkpad.ui.screens.home

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ark.arkpad.alert
import com.ark.arkpad.db.AppDatabase
import com.ark.arkpad.db.DeviceDao
import com.ark.arkpad.model.Device
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeviceViewModel(private val deviceDao: DeviceDao) : ViewModel() {
    val devices = deviceDao
        .getAllDevices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    fun tryInsertDevice(context: Context, device: Device) {
        viewModelScope.launch {
            try {
                deviceDao.insertDevice(device)
            } catch (_: SQLiteConstraintException) {
                alert(context, "This device already exists.")
            }
        }
    }

    fun deleteSelectedDevices(context: Context, devices: List<Device>) {
        viewModelScope.launch {
            deviceDao.deleteDevices(devices)
        }
        val suffix = if (devices.size == 1) "" else "s"
        alert(context, "${devices.size} device$suffix deleted.")
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY]!!
                val deviceDao = AppDatabase.getInstance(app).deviceDao()
                DeviceViewModel(deviceDao)
            }
        }
    }
}