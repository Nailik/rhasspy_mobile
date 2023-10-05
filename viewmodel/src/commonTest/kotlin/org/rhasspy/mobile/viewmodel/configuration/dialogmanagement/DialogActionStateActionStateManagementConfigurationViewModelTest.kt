package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DialogActionStateActionStateManagementConfigurationViewModelTest : AppTest() {

    private lateinit var pipelineConfigurationViewModel: PipelineConfigurationViewModel

    private lateinit var initialPipelineConfigurationData: PipelineConfigurationData
    private lateinit var pipelineConfigurationData: PipelineConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialPipelineConfigurationData = PipelineConfigurationData(
            pipelineManagerOption = PipelineManagerOption.Local,
            textAsrTimeout = 10000L,
            intentRecognitionTimeout = 10000L,
            recordingTimeout = 10000L
        )

        pipelineConfigurationData = PipelineConfigurationData(
            pipelineManagerOption = PipelineManagerOption.Disabled,
            textAsrTimeout = 234,
            intentRecognitionTimeout = 234,
            recordingTimeout = 234
        )

        pipelineConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialPipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)

        with(pipelineConfigurationData) {
            pipelineConfigurationViewModel.onEvent(ChangeIntentRecognitionTimeout(recordingTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(ChangeRecordingTimeout(intentRecognitionTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(ChangeTextAsrTimeout(textAsrTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(SelectPipelineOption(pipelineManagerOption))
        }

        assertEquals(pipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)

        pipelineConfigurationViewModel.onEvent(Save)

        assertEquals(pipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)
        assertEquals(pipelineConfigurationData, PipelineConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialPipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)

        with(pipelineConfigurationData) {
            pipelineConfigurationViewModel.onEvent(ChangeIntentRecognitionTimeout(recordingTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(ChangeRecordingTimeout(intentRecognitionTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(ChangeTextAsrTimeout(textAsrTimeout.toStringOrEmpty()))
            pipelineConfigurationViewModel.onEvent(SelectPipelineOption(pipelineManagerOption))
        }

        assertEquals(pipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)

        pipelineConfigurationViewModel.onEvent(Discard)

        assertEquals(initialPipelineConfigurationData, pipelineConfigurationViewModel.viewState.value.editData)
        assertEquals(initialPipelineConfigurationData, PipelineConfigurationData())
    }
}