package org.rhasspy.mobile.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.android.hiddenScreens.AboutScreen
import org.rhasspy.mobile.android.bottomBarScreens.HiddenScreens
import org.rhasspy.mobile.android.hiddenScreens.LibrariesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenScreensNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.padding(end = 16.dp),
                title = { Text("About app") },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HiddenScreens.AboutScreen.name,
            modifier = Modifier.padding(
                paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                paddingValues.calculateTopPadding(),
                paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                paddingValues.calculateBottomPadding()
            )
        ) {
            composable(HiddenScreens.AboutScreen.name) {
                AboutScreen(navController)
            }
            composable(HiddenScreens.LibrariesScreen.name) {
                LibrariesScreen()
            }
        }
    }
}
