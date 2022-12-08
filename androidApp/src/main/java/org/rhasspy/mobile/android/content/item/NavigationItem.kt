package org.rhasspy.mobile.android.content.item

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag

@Composable
fun RowScope.NavigationItem(screen: Enum<*>, icon: @Composable () -> Unit, label: @Composable () -> Unit) {

    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBarItem(
        modifier = Modifier.testTag(screen),
        selected = currentDestination?.hierarchy?.any { it.route?.startsWith(screen.name) == true } == true,
        onClick = {
            navController.navigate(screen.name) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = icon,
        label = label
    )

}