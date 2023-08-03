package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
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
            isUseCustomSpeechToTextHttpEndpoint = false,
            isUseSpeechToTextMqttSilenceDetection = true,
            speechToTextHttpEndpoint = ""
        )

        speechToTextConfigurationData = SpeechToTextConfigurationData(
            speechToTextOption = SpeechToTextOption.RemoteMQTT,
            isUseCustomSpeechToTextHttpEndpoint = true,
            isUseSpeechToTextMqttSilenceDetection = false,
            speechToTextHttpEndpoint = getRandomString(5)
        )

        speechToTextConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialSpeechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )

        with(speechToTextConfigurationData) {
            speechToTextConfigurationViewModel.onEvent(SelectSpeechToTextOption(speechToTextOption))
            speechToTextConfigurationViewModel.onEvent(
                SetUseCustomHttpEndpoint(
                    isUseCustomSpeechToTextHttpEndpoint
                )
            )
            speechToTextConfigurationViewModel.onEvent(
                SetUseSpeechToTextMqttSilenceDetection(
                    isUseSpeechToTextMqttSilenceDetection
                )
            )
            speechToTextConfigurationViewModel.onEvent(
                UpdateSpeechToTextHttpEndpoint(
                    speechToTextHttpEndpoint
                )
            )
        }

        assertEquals(
            speechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )

        speechToTextConfigurationViewModel.onEvent(Save)

        assertEquals(
            speechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )
        assertEquals(speechToTextConfigurationData, SpeechToTextConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialSpeechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )

        with(speechToTextConfigurationData) {
            speechToTextConfigurationViewModel.onEvent(SelectSpeechToTextOption(speechToTextOption))
            speechToTextConfigurationViewModel.onEvent(
                SetUseCustomHttpEndpoint(
                    isUseCustomSpeechToTextHttpEndpoint
                )
            )
            speechToTextConfigurationViewModel.onEvent(
                SetUseSpeechToTextMqttSilenceDetection(
                    isUseSpeechToTextMqttSilenceDetection
                )
            )
            speechToTextConfigurationViewModel.onEvent(
                UpdateSpeechToTextHttpEndpoint(
                    speechToTextHttpEndpoint
                )
            )
        }

        assertEquals(
            speechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )

        speechToTextConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialSpeechToTextConfigurationData,
            speechToTextConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialSpeechToTextConfigurationData, SpeechToTextConfigurationData())
    }
}