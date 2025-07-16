package com.ark.arkpad.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ark.arkpad.model.Device
import com.ark.arkpad.ui.components.FadeIn
import com.ark.arkpad.ui.components.multiselect.MultiSelectableList
import com.ark.arkpad.ui.components.multiselect.MultiSelectableScreen

@Composable
fun HomeScreen(
    deviceViewModel: DeviceViewModel,
    onNavigateToController: (Device) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val context = LocalContext.current
    val devices by deviceViewModel.devices.collectAsState()
    val selectedDevices = remember { mutableStateListOf<Device>() }
    val selectionState = remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }

    if (isDialogVisible) {
        NewDeviceDialog(
            onConfirm = { device ->
                deviceViewModel.tryInsertDevice(context, device)
                isDialogVisible = false
            },
            onDismiss = { isDialogVisible = false },
        )
    }

    val clearSelection = {
        selectedDevices.clear()
        selectionState.value = false
    }

    MultiSelectableScreen(
        title = "Devices",
        selectionState = selectionState,
        selectedItems = selectedDevices,
        onSelectionExit = clearSelection,
        floatingActionButton = {
            FloatingActionButton(onClick = { isDialogVisible = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a new device.",
                )
            }
        },
        actions = {
            if (selectionState.value) {
                IconButton(onClick = {
                    deviceViewModel.deleteSelectedDevices(context, selectedDevices)
                    clearSelection()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete selected devices.",
                    )
                }
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Navigate to settings.",
                )
            }
        },
    ) { innerPadding ->
        if (devices == null)
            return@MultiSelectableScreen

        FadeIn(modifier = Modifier.padding(innerPadding)) {
            if (devices!!.isEmpty()) {
                Placeholder(
                    icon = Icons.AutoMirrored.Outlined.List,
                    text = "Device list is empty.",
                )
            } else {
                MultiSelectableList(
                    selectionState = selectionState,
                    items = devices!!,
                    onItemClick = { device -> onNavigateToController(device) },
                    selectedItems = selectedDevices,
                    itemIcon = Icons.Outlined.DesktopWindows,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxSize(),
                )
            }
        }
    }
}