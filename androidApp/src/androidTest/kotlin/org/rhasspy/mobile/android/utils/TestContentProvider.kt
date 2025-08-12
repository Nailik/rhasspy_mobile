package org.rhasspy.mobile.android.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory

@Composable
fun KoinComponent.TestContentProvider(
    content: @Composable () -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackBarHostState provides snackbarHostState,
        LocalViewModelFactory provides get(),
    ) {
        content()
    }

}