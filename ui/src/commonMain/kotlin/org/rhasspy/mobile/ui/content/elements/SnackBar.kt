package org.rhasspy.mobile.ui.content.elements

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.ui.LocalSnackBarHostState

@Composable
fun SnackBar(
    title: StableStringResource,
    label: StableStringResource? = null,
    action: (() -> Unit)? = null,
    consumed: () -> Unit,
) {

    val snackBarHostState = LocalSnackBarHostState.current
    val snackBarMessage = translate(title)
    val snackBarActionLabel = label?.let { translate(it) }

    LaunchedEffect(Unit) {
        val snackBarResult = snackBarHostState.showSnackbar(
            message = snackBarMessage,
            actionLabel = snackBarActionLabel,
            duration = SnackbarDuration.Short,
        )

        consumed()

        if (snackBarResult == SnackbarResult.ActionPerformed) {
            action?.invoke()
        }
    }

}