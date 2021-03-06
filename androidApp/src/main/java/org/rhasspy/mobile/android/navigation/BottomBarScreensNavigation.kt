package org.rhasspy.mobile.android.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.bottomBarScreens.*
import org.rhasspy.mobile.android.permissions.MicrophonePermissionRequired
import org.rhasspy.mobile.android.permissions.OverlayPermissionRequired
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.viewModels.GlobalData
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.BottomBarScreensNavigation(viewModel: HomeScreenViewModel = viewModel(), mainNavController: NavController) {
    var isBottomNavigationHidden by remember { mutableStateOf(false) }

    isBottomNavigationHidden = this.maxHeight < 250.dp

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopAppBar(viewModel, snackbarHostState, navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            //hide bottom navigation with keyboard and small screens
            if (!isBottomNavigationHidden) {
                BottomNavigation(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreens.HomeScreen.name,
            modifier = Modifier.padding(
                paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                paddingValues.calculateTopPadding(),
                paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                paddingValues.calculateBottomPadding()
            )
        ) {
            composable(BottomBarScreens.HomeScreen.name) {
                HomeScreen(snackbarHostState)
            }
            composable(BottomBarScreens.ConfigurationScreen.name) {
                ConfigurationScreen(snackbarHostState)
            }
            composable(BottomBarScreens.SettingsScreen.name) {
                SettingsScreen(snackbarHostState, mainNavController)
            }
            composable(BottomBarScreens.LogScreen.name) {
                LogScreen()
            }
        }
    }
}

@Composable
fun TopAppBar(viewModel: HomeScreenViewModel, snackbarHostState: SnackbarHostState, navController: NavHostController) {
    SmallTopAppBar(
        modifier = Modifier.padding(end = 16.dp),
        title = { Text(MR.strings.appName, modifier = Modifier.testTag("appName")) },
        actions = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()

            when (navBackStackEntry?.destination?.route) {
                BottomBarScreens.HomeScreen.name,
                BottomBarScreens.ConfigurationScreen.name -> HomeAndConfigScreenActions(viewModel, snackbarHostState)
                BottomBarScreens.SettingsScreen.name -> {}
                BottomBarScreens.LogScreen.name -> LogScreenActions(viewModel)
            }
        }
    )
}


@Composable
fun BottomNavigation(navController: NavHostController) {
    NavigationBar {

        val array = mutableListOf(BottomBarScreens.HomeScreen, BottomBarScreens.ConfigurationScreen, BottomBarScreens.SettingsScreen)

        if (AppSettings.isShowLog.observe()) {
            array.add(BottomBarScreens.LogScreen)
        }

        array.forEach { screen ->
            NavigationItem(screen, navController)
        }
    }
}

@Composable
fun RowScope.NavigationItem(screen: BottomBarScreens, navController: NavHostController) {
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
fun HomeAndConfigScreenActions(viewModel: HomeScreenViewModel, snackbarHostState: SnackbarHostState) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        MicrophonePermissionRequired(viewModel, snackbarHostState)
        OverlayPermissionRequired(viewModel)
        UnsavedChanges(viewModel)
    }
}


@Composable
fun UnsavedChanges(viewModel: HomeScreenViewModel) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = GlobalData.unsavedChanges.observe()
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
        visible = viewModel.currentServiceState.observe() != ServiceState.Running
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
