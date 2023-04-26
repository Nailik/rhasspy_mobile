package org.rhasspy.mobile.android.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.main.LocalViewModelFactory

@Composable
fun KoinComponent.TestContentProvider(
    navController: NavHostController = rememberNavController(),
    content: @Composable () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
        LocalMainNavController provides navController,
        LocalViewModelFactory provides get(),
    ) {
        content()
    }

}