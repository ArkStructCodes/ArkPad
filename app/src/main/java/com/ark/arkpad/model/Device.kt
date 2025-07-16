package com.ark.arkpad.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ark.arkpad.ui.components.multiselect.ItemInfo
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "devices")
data class Device(
    @PrimaryKey
    override val name: String,
    val host: String,
    val port: Int,
) : ItemInfo {
    override val description
        get() = "$host:$port"
}