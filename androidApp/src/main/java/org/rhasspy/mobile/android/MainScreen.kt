package org.rhasspy.mobile.android

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(topBar = { TopAppBar() },
        bottomBar = { BottomNavigation(navController) }
    ) { NavHost(navController) }
}

@Composable
private fun NavHost(navController: NavHostController) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.name
    ) {
        composable(Screens.HomeScreen.name) {
            HomeScreen()
        }
        composable(Screens.ConfigurationScreen.name) {
            ConfigurationScreen()
        }
        composable(Screens.SettingsScreen.name) {
            SettingsScreen()
        }
        composable(Screens.LogScreen.name) {
            LogScreen()
        }
    }
}

@Composable
fun TopAppBar() {
    MediumTopAppBar(
        title = { Text(text = "hello2") }
    )
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        arrayOf(Screens.HomeScreen, Screens.ConfigurationScreen, Screens.SettingsScreen, Screens.LogScreen).forEach { screen ->
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
    }
}