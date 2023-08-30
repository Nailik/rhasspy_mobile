package org.rhasspy.mobile.ui.content

import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Dp

@Composable
fun CustomElevation(
    tonalElevation: Dp,
    content: @Composable () -> Unit
) {
    val absoluteElevation = LocalAbsoluteTonalElevation.current + tonalElevation

    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {

        content()

    }

}

@Composable
fun CustomAbsoluteElevation(
    absoluteElevation: Dp,
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {

        content()

    }

}