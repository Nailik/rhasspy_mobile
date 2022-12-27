package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.NavigationItem
import org.rhasspy.mobile.android.navigation.BottomBarScreenType
import org.rhasspy.mobile.android.navigation.NavigationParams
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.viewModels.screens.HomeScreenViewModel

val LocalConfigurationNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalMainNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

/**
 * navigation holder for bottom navigation bar screens
 */
@Composable
fun BottomBarScreensNavigation(viewModel: HomeScreenViewModel = get()) {

    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BottomNavigation(viewModel, navController) }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = BottomBarScreenType.HomeScreen.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomBarScreenType.HomeScreen.name) {
                    HomeScreen()
                }
                composable(
                    BottomBarScreenType.ConfigurationScreen
                        .appendOptionalParameter(
                            NavigationParams.ScrollToError,
                            "{${NavigationParams.ScrollToError.name}}"
                        ),
                    arguments = listOf(navArgument(NavigationParams.ScrollToError.name) {
                        defaultValue = false
                    })
                ) {
                    ConfigurationScreen(
                        scrollToError = it.arguments?.getBoolean(NavigationParams.ScrollToError.name)
                            ?: false
                    )
                }
                composable(BottomBarScreenType.SettingsScreen.name) {
                    SettingsScreen()
                }
                composable(BottomBarScreenType.LogScreen.name) {
                    LogScreen()
                }
            }
        }
    }
}

/**
 * navigation bar on bottom
 */
@Composable
fun BottomNavigation(viewModel: HomeScreenViewModel, navController: NavController) {

    NavigationBar {

        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationItem(
            screen = BottomBarScreenType.HomeScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreenType.HomeScreen.name) {
                        Icons.Filled.Mic
                    } else {
                        Icons.Outlined.Mic
                    }, MR.strings.home
                )
            },
            label = { Text(MR.strings.home) }
        )

        NavigationItem(
            screen = BottomBarScreenType.ConfigurationScreen,
            icon = {
                Icon(
                    painterResource(MR.images.ic_launcher.drawableResId),
                    MR.strings.configuration,
                    Modifier.size(24.dp)
                )
            },
            label = { Text(MR.strings.configuration) }
        )

        NavigationItem(
            screen = BottomBarScreenType.SettingsScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreenType.SettingsScreen.name) {
                        Icons.Filled.Settings
                    } else {
                        Icons.Outlined.Settings
                    }, MR.strings.settings
                )
            },
            label = { Text(MR.strings.settings) }
        )

        if (viewModel.isShowLogEnabled.collectAsState().value) {
            NavigationItem(screen = BottomBarScreenType.LogScreen,
                icon = { Icon(Icons.Filled.Code, MR.strings.log) },
                label = { Text(MR.strings.log) }
            )
        }

    }

}