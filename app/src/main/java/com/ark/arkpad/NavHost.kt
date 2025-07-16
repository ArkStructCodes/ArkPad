package com.ark.arkpad

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ark.arkpad.model.Device
import com.ark.arkpad.preferences.PreferencesRepository
import com.ark.arkpad.ui.screens.controller.ControllerScreen
import com.ark.arkpad.ui.screens.controller.ControllerViewModel
import com.ark.arkpad.ui.screens.home.DeviceViewModel
import com.ark.arkpad.ui.screens.home.HomeScreen
import com.ark.arkpad.ui.screens.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Settings

@Composable
fun ArkPadNavHost(
    preferences: PreferencesRepository,
    navController: NavHostController = rememberNavController(),
) {
    val activity = LocalActivity.current

    NavHost(navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                deviceViewModel = viewModel(factory = DeviceViewModel.Factory),
                onNavigateToController = { device ->
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                    navController.navigate(device)
                },
                onNavigateToSettings = { navController.navigate(Settings) },
            )
        }
        composable<Settings> {
            SettingsScreen(
                preferences,
                onNavigateBack = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Device> {
            val device = it.toRoute<Device>()
            val controllerViewModel = viewModel<ControllerViewModel>(
                viewModelStoreOwner = it,
                factory = viewModelFactory {
                    initializer { ControllerViewModel(device, preferences) }
                },
            )

            ControllerScreen(
                controllerViewModel,
                preferences,
                onNavigateBack = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                        launchSingleTop = true
                    }
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                },
            )
        }
    }
}