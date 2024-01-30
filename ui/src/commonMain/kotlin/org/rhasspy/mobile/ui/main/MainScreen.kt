package org.rhasspy.mobile.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.LocalSnackBarHostState
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.CloseChangelog
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.CrashlyticsDialogResult
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppTheme {
            //fixes bright flashing when navigating between screens
            Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

                val snackBarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    LocalSnackBarHostState provides snackBarHostState,
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackBarHostState) },
                    ) { paddingValues ->


                        Box(modifier = Modifier.padding(paddingValues)) {

                            MainScreenContent(viewModel)

                        }

                    }
                }
            }
        }

        if (isDebug()) {
            Text(
                text = "DEBUG",
                modifier = Modifier
                    .rotate(45F)
                    .offset(50.dp)
                    .background(Color.Red)
                    .width(180.dp)
                    .align(Alignment.TopEnd),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MainScreenContent(viewModel: MainScreenViewModel) {
    ScreenContent(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        if (viewState.isShowCrashlyticsDialog) {
            CrashlyticsDialog(
                onResult = {
                    viewModel.onEvent(
                        CrashlyticsDialogResult(it)
                    )
                }
            )
        }

        if (viewState.isChangelogDialogVisible) {
            ChangelogDialog(
                changelog = viewState.changelog,
                onDismissRequest = { viewModel.onEvent(CloseChangelog) }
            )
        }

        val screen by viewModel.screen.collectAsState()
        NavigationContent(screen = screen)
    }
}


/**
 * Displays changelog as text in a dialog
 */
@Composable
private fun ChangelogDialog(
    changelog: ImmutableList<String>,
    onDismissRequest: () -> Unit
) {

    Dialog(
        testTag = TestTag.DialogChangelog,
        title = "${translate(MR.strings.changelog.stable)} - ${BuildKonfig.versionName}",
        supportingText = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(changelog) { item ->
                    Text(text = item)
                }
            }
        },
        confirmLabel = MR.strings.close.stable,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest
    )

}

/**
 * dialog if user wants to enable crashlytics
 */
@Composable
fun CrashlyticsDialog(onResult: (result: Boolean) -> Unit) {
    Dialog(
        testTag = TestTag.DialogCrashlytics,
        icon = Icons.Filled.BugReport,
        title = MR.strings.crashlytics.stable,
        message = MR.strings.crashlyticsDialogText.stable,
        confirmLabel = MR.strings.confirm.stable,
        dismissLabel = MR.strings.deny.stable,
        onConfirm = { onResult(true) },
        onDismiss = { onResult(false) },
        onClose = {}
    )
}