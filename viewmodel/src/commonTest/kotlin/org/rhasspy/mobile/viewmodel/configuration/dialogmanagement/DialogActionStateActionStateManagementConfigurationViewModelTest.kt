package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.ChangeIntentRecognitionTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.ChangeRecordingTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.ChangeTextAsrTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DialogActionStateActionStateManagementConfigurationViewModelTest : AppTest() {

    private lateinit var dialogManagementConfigurationViewModel: DialogManagementConfigurationViewModel

    private lateinit var initialDialogManagementConfigurationData: DialogManagementConfigurationData
    private lateinit var dialogManagementConfigurationData: DialogManagementConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialDialogManagementConfigurationData = DialogManagementConfigurationData(
            dialogManagementOption = DialogManagementOption.Local,
            textAsrTimeout = 10000L,
            intentRecognitionTimeout = 10000L,
            recordingTimeout = 10000L
        )

        dialogManagementConfigurationData = DialogManagementConfigurationData(
            dialogManagementOption = DialogManagementOption.Disabled,
            textAsrTimeout = 234,
            intentRecognitionTimeout = 234,
            recordingTimeout = 234
        )

        dialogManagementConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialDialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )

        with(dialogManagementConfigurationData) {
            dialogManagementConfigurationViewModel.onEvent(
                ChangeIntentRecognitionTimeout(
                    recordingTimeout.toStringOrEmpty()
                )
            )
            dialogManagementConfigurationViewModel.onEvent(
                ChangeRecordingTimeout(
                    intentRecognitionTimeout.toStringOrEmpty()
                )
            )
            dialogManagementConfigurationViewModel.onEvent(ChangeTextAsrTimeout(textAsrTimeout.toStringOrEmpty()))
            dialogManagementConfigurationViewModel.onEvent(
                SelectDialogManagementOption(
                    dialogManagementOption
                )
            )
        }

        assertEquals(
            dialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )

        dialogManagementConfigurationViewModel.onEvent(Save)

        assertEquals(
            dialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )
        assertEquals(dialogManagementConfigurationData, DialogManagementConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialDialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )

        with(dialogManagementConfigurationData) {
            dialogManagementConfigurationViewModel.onEvent(
                ChangeIntentRecognitionTimeout(
                    recordingTimeout.toStringOrEmpty()
                )
            )
            dialogManagementConfigurationViewModel.onEvent(
                ChangeRecordingTimeout(
                    intentRecognitionTimeout.toStringOrEmpty()
                )
            )
            dialogManagementConfigurationViewModel.onEvent(ChangeTextAsrTimeout(textAsrTimeout.toStringOrEmpty()))
            dialogManagementConfigurationViewModel.onEvent(
                SelectDialogManagementOption(
                    dialogManagementOption
                )
            )
        }

        assertEquals(
            dialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )

        dialogManagementConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialDialogManagementConfigurationData,
            dialogManagementConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialDialogManagementConfigurationData, DialogManagementConfigurationData())
    }
}