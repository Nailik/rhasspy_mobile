package org.rhasspy.mobile.ui.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackPressHandler(onBackClick: () -> Unit) {
    BackHandler(onBack = onBackClick)
}