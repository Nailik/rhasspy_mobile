package org.rhasspy.mobile.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
actual fun SetSystemColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(color) {
        systemUiController.setSystemBarsColor(color)
        systemUiController.setNavigationBarColor(color)
        systemUiController.setStatusBarColor(color)
    }
}