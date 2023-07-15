package org.rhasspy.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.main.DialogTransitionListItem
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState.SourceViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState.SourceViewState.SourceType.Local
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogStateViewState

@Preview
@Composable
private fun DialogTransitionListItemPreview() {
    DialogTransitionListItem(
        DialogTransitionItem(
            action = DialogActionViewState(
                name = MR.strings.asr_error.stable,
                source = SourceViewState(
                    name = MR.strings.asr_error.stable,
                    type = Local
                ),
                information = MR.strings.asr_error.stable,
            ),
            state = DialogStateViewState(
                name = MR.strings.asr_error.stable,
                sessionData = SessionData(
                    sessionId = "qwertz",
                    sendAudioCaptured = false,
                    wakeWord = "preview",
                    recognizedText = null
                )
            )
        )
    )
}