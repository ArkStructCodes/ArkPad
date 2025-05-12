package com.ark.arkpad

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ark.arkpad.model.Device
import com.ark.arkpad.ui.screens.controller.ControllerViewModel
import com.ark.arkpad.ui.screens.controller.ControllerScreen
import com.ark.arkpad.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Composable
fun NavProvider() {
    val activity = LocalActivity.current
    val navController = rememberNavController()

    NavHost(navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(onNavigateToController = { device ->
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                navController.navigate(device)
            })
        }
        composable<Device> { backStackEntry ->
            val device = backStackEntry.toRoute<Device>()
            val controllerViewModel = viewModel<ControllerViewModel>(
                viewModelStoreOwner = backStackEntry,
                factory = viewModelFactory {
                    initializer { ControllerViewModel(device) }
                },
            )

            ControllerScreen(
                controllerViewModel,
                onNavigateToHome = {
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