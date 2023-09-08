package org.rhasspy.mobile.android.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import org.rhasspy.mobile.ui.content.LocalSnackBarHostState

@Composable
fun TestContentProvider(
    content: @Composable () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackBarHostState provides snackbarHostState,
    ) {
        content()
    }

}