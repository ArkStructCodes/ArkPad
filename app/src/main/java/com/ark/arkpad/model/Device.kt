package com.ark.arkpad.model

import androidx.room.Entity
import com.ark.arkpad.ui.components.DisplayItem
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "devices", primaryKeys = ["name", "host", "port"])
data class Device(
    override val name: String,
    val host: String,
    val port: Int,
) : DisplayItem {
    override val description
        get() = "$host:$port"
}