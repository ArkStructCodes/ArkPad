package com.ark.arkpad.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ark.arkpad.model.Device
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Insert
    suspend fun insertDevice(device: Device)

    @Delete
    suspend fun deleteDevices(devices: List<Device>)

    @Query("SELECT * from devices")
    fun getAllDevices(): Flow<List<Device>>
}