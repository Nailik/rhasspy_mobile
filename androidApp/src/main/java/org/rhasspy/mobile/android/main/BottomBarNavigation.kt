package org.rhasspy.mobile.android.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
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
enum class BottomBarScreens(val icon: @Composable () -> Unit, val label: @Composable () -> Unit) {
    HomeScreen({ Icon(Icons.Filled.Mic, MR.strings.home) }, { Text(MR.strings.home) }),
    ConfigurationScreen(
        { Icon(painterResource(MR.images.ic_launcher.drawableResId), MR.strings.configuration, Modifier.size(24.dp)) },
        { Text(MR.strings.configuration) }),
    SettingsScreen({ Icon(Icons.Filled.Settings, MR.strings.settings) }, { Text(MR.strings.settings) }),
    LogScreen({ Icon(Icons.Filled.Code, MR.strings.log) }, { Text(MR.strings.log) })
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

        val array = mutableListOf(BottomBarScreens.HomeScreen, BottomBarScreens.ConfigurationScreen, BottomBarScreens.SettingsScreen)

        if (viewModel.isShowLogEnabled.collectAsState().value) {
            array.add(BottomBarScreens.LogScreen)
        }

        array.forEach { screen ->
            NavigationItem(screen)
        }
    }

}

@Composable
fun RowScope.NavigationItem(screen: BottomBarScreens) {

    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBarItem(
        modifier = Modifier.testTag(screen),
        selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
        onClick = {
            navController.navigate(screen.name) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = screen.icon,
        label = screen.label
    )

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
