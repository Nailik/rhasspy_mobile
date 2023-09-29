package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SpeechToTextConfigurationViewModelTest : AppTest() {

    private lateinit var speechToTextConfigurationViewModel: SpeechToTextConfigurationViewModel

    private lateinit var initialSpeechToTextConfigurationData: SpeechToTextConfigurationData
    private lateinit var speechToTextConfigurationData: SpeechToTextConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialSpeechToTextConfigurationData = SpeechToTextConfigurationData(
            speechToTextOption = SpeechToTextOption.Disabled,
            isUseSpeechToTextMqttSilenceDetection = true
        )

        speechToTextConfigurationData = SpeechToTextConfigurationData(
            speechToTextOption = SpeechToTextOption.Rhasspy2HermesMQTT,
            isUseSpeechToTextMqttSilenceDetection = false
        )

        speechToTextConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialSpeechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)

        with(speechToTextConfigurationData) {
            speechToTextConfigurationViewModel.onEvent(SelectSpeechToTextOption(speechToTextOption))
            speechToTextConfigurationViewModel.onEvent(SetUseSpeechToTextMqttSilenceDetection(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(speechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)

        speechToTextConfigurationViewModel.onEvent(Save)

        assertEquals(speechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)
        assertEquals(speechToTextConfigurationData, SpeechToTextConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialSpeechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)

        with(speechToTextConfigurationData) {
            speechToTextConfigurationViewModel.onEvent(SelectSpeechToTextOption(speechToTextOption))
            speechToTextConfigurationViewModel.onEvent(SetUseSpeechToTextMqttSilenceDetection(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(speechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)

        speechToTextConfigurationViewModel.onEvent(Discard)

        assertEquals(initialSpeechToTextConfigurationData, speechToTextConfigurationViewModel.viewState.value.editData)
        assertEquals(initialSpeechToTextConfigurationData, SpeechToTextConfigurationData())
    }
}