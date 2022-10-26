package org.rhasspy.mobile.android.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.NavigationItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

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
 * screens in the bottom bar
 */
enum class BottomBarScreens() {
    HomeScreen,
    ConfigurationScreen,
    SettingsScreen,
    LogScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.BottomBarScreensNavigation(viewModel: HomeScreenViewModel = viewModel()) {
    var isBottomNavigationHidden by remember { mutableStateOf(false) }

    isBottomNavigationHidden = this.maxHeight < 250.dp

    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppBar() },
            snackbarHost = { SnackbarHost(snackbarHostState, modifier = Modifier.testTag("test)")) },
            bottomBar = {
                //hide bottom navigation with keyboard and small screens
                if (!isBottomNavigationHidden) {
                    BottomNavigation(viewModel)
                }
            }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = BottomBarScreens.HomeScreen.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomBarScreens.HomeScreen.name) {
                    HomeScreen()
                }
                composable(BottomBarScreens.ConfigurationScreen.name) {
                    ConfigurationScreen()
                }
                composable(BottomBarScreens.SettingsScreen.name) {
                    SettingsScreen()
                }
                composable(BottomBarScreens.LogScreen.name) {
                    LogScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar() {

    var currentDestination by remember { mutableStateOf(BottomBarScreens.HomeScreen) }
    val navController = LocalNavController.current

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = BottomBarScreens.valueOf(destination.route ?: BottomBarScreens.HomeScreen.name)
        }
    }

    val title = when (currentDestination) {
        BottomBarScreens.HomeScreen -> MR.strings.appName
        BottomBarScreens.ConfigurationScreen -> MR.strings.configuration
        BottomBarScreens.SettingsScreen -> MR.strings.settings
        BottomBarScreens.LogScreen -> MR.strings.log
    }

    MediumTopAppBar(
        title = { Text(title) }
    )

}

@Composable
fun BottomNavigation(viewModel: HomeScreenViewModel) {

    NavigationBar {

        NavigationItem(screen = BottomBarScreens.HomeScreen,
            icon = { Icon(Icons.Filled.Mic, MR.strings.home) },
            label = { Text(MR.strings.home) })

        NavigationItem(screen = BottomBarScreens.ConfigurationScreen,
            icon = { Icon(painterResource(MR.images.ic_launcher.drawableResId), MR.strings.configuration, Modifier.size(24.dp)) },
            label = { Text(MR.strings.configuration) })

        NavigationItem(screen = BottomBarScreens.SettingsScreen,
            icon = { Icon(Icons.Filled.Settings, MR.strings.settings) },
            label = { Text(MR.strings.settings) })

        if (viewModel.isShowLogEnabled.collectAsState().value) {
            NavigationItem(screen = BottomBarScreens.LogScreen,
                icon = { Icon(Icons.Filled.Code, MR.strings.log) },
                label = { Text(MR.strings.log) })
        }

    }

}

@Composable
fun UnsavedChanges(viewModel: HomeScreenViewModel) {

    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = false
    ) {

    Row(modifier = Modifier.padding(start = 8.dp)) {
            IconButton(onClick = { viewModel.resetChanges() })
            {
                Icon(
                    imageVector = Icons.Filled.Restore,
                    contentDescription = MR.strings.reset
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.saveAndApplyChanges() }) {
                Icon(
                    imageVector = Icons.Filled.PublishedWithChanges,
                    contentDescription = MR.strings.save
                )
            }
        }

    }

    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = viewModel.currentServiceState.collectAsState().value != ServiceState.Running
    ) {

        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 360F,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing)
            )
        )

        Icon(
            modifier = Modifier.rotate(angle),
            imageVector = Icons.Filled.Autorenew,
            contentDescription = MR.strings.reset
        )

    }

}
