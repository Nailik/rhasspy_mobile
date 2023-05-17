package org.rhasspy.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.rhasspy.mobile.viewmodel.KViewModel

@Composable
fun Screen(
    viewModel: KViewModel,
    content: @Composable () -> Unit
) {

    DisposableEffect(viewModel) {
        viewModel.composed()
        onDispose {
            viewModel.disposed()
        }
    }

    content()
}