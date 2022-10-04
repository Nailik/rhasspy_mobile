package org.rhasspy.mobile.android.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.bottomBarScreens.ConfigurationScreen
import org.rhasspy.mobile.android.bottomBarScreens.HomeScreen
import org.rhasspy.mobile.android.bottomBarScreens.LogScreen
import org.rhasspy.mobile.android.bottomBarScreens.LogScreenActions
import org.rhasspy.mobile.android.permissions.MicrophonePermissionRequired
import org.rhasspy.mobile.android.permissions.OverlayPermissionRequired
import org.rhasspy.mobile.android.settingsScreen.SettingsScreen
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData
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
            topBar = { TopAppBar(viewModel) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
fun TopAppBar(viewModel: HomeScreenViewModel) {
    TopAppBar(
        modifier = Modifier.padding(end = 16.dp),
        title = { Text(MR.strings.appName, modifier = Modifier.testTag("appName")) },
        actions = {

            val navBackStackEntry by LocalNavController.current.currentBackStackEntryAsState()

            when (navBackStackEntry?.destination?.route) {
                BottomBarScreens.HomeScreen.name,
                BottomBarScreens.ConfigurationScreen.name -> HomeAndConfigScreenActions(viewModel)

                BottomBarScreens.SettingsScreen.name -> {}
                BottomBarScreens.LogScreen.name -> LogScreenActions(viewModel)
            }
        }
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
    NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
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
fun HomeAndConfigScreenActions(viewModel: HomeScreenViewModel) {
    Row(modifier = Modifier.padding(start = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MicrophonePermissionRequired(viewModel)
        OverlayPermissionRequired(viewModel)
        MqttConnectionStatus()
        UnsavedChanges(viewModel)
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MqttConnectionStatus() {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = ConfigurationSettings.isMQTTEnabled.data.collectAsState().value && !MqttService.isConnected.collectAsState().value
    ) {

        var openDialog by rememberSaveable { mutableStateOf(false) }

        IconButton(
            onClick = { openDialog = true },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            org.rhasspy.mobile.android.utils.Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = MR.strings.notConnected
            )
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                confirmButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text(MR.strings.ok)
                    }
                },
                title = {
                    Text(MR.strings.mqttNotConnected)
                },
                text = {
                    MqttService.hasConnectionError.collectAsState().value?.also {
                        val text = translate(MR.strings.mqttConnectionError)
                        Text("$text\n${it.msg} (${it.statusCode})")
                    }

                    MqttService.hasConnectionLostError.collectAsState().value?.also {
                        val text = translate(MR.strings.mqttConnectionLostError)
                        Text("$text\n${it.message}")
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                properties = DialogProperties(usePlatformDefaultWidth = false)
            )
        }
    }
}

@Composable
fun UnsavedChanges(viewModel: HomeScreenViewModel) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = GlobalData.unsavedChanges.collectAsState().value
    ) {
        Row(modifier = Modifier.padding(start = 8.dp)) {
            IconButton(onClick = { viewModel.resetChanges() })
            {
                org.rhasspy.mobile.android.utils.Icon(
                    imageVector = Icons.Filled.Restore,
                    contentDescription = MR.strings.reset
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.saveAndApplyChanges() }) {
                org.rhasspy.mobile.android.utils.Icon(
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

        org.rhasspy.mobile.android.utils.Icon(
            modifier = Modifier.rotate(angle),
            imageVector = Icons.Filled.Autorenew,
            contentDescription = MR.strings.reset
        )
    }
}
