package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change.SelectAsrOption
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change.SetUseAsrMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewState.AsrConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SpeechToTextConfigurationViewModelTest : AppTest() {

    private lateinit var asrConfigurationViewModel: AsrConfigurationViewModel

    private lateinit var initialAsrConfigurationData: AsrConfigurationData
    private lateinit var asrConfigurationData: AsrConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialAsrConfigurationData = AsrConfigurationData(
            asrDomainOption = AsrDomainOption.Disabled,
            isUseSpeechToTextMqttSilenceDetection = true
        )

        asrConfigurationData = AsrConfigurationData(
            asrDomainOption = AsrDomainOption.Rhasspy2HermesMQTT,
            isUseSpeechToTextMqttSilenceDetection = false
        )

        asrConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialAsrConfigurationData, asrConfigurationViewModel.viewState.value.editData)

        with(asrConfigurationData) {
            asrConfigurationViewModel.onEvent(SelectAsrOption(asrDomainOption))
            asrConfigurationViewModel.onEvent(SetUseAsrMqttSilenceDetection(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(asrConfigurationData, asrConfigurationViewModel.viewState.value.editData)

        asrConfigurationViewModel.onEvent(Save)

        assertEquals(asrConfigurationData, asrConfigurationViewModel.viewState.value.editData)
        assertEquals(asrConfigurationData, AsrConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialAsrConfigurationData, asrConfigurationViewModel.viewState.value.editData)

        with(asrConfigurationData) {
            asrConfigurationViewModel.onEvent(SelectAsrOption(asrDomainOption))
            asrConfigurationViewModel.onEvent(SetUseAsrMqttSilenceDetection(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(asrConfigurationData, asrConfigurationViewModel.viewState.value.editData)

        asrConfigurationViewModel.onEvent(Discard)

        assertEquals(initialAsrConfigurationData, asrConfigurationViewModel.viewState.value.editData)
        assertEquals(initialAsrConfigurationData, AsrConfigurationData())
    }
}