package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrDomainConfigurationUiEvent.Change.SelectAsrOptionDomain
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrDomainConfigurationUiEvent.Change.SetUseAsrMqttSilenceDetectionDomain
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrDomainConfigurationViewState.AsrDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SpeechToTextConfigurationViewModelTest : AppTest() {

    private lateinit var asrDomainConfigurationViewModel: AsrDomainConfigurationViewModel

    private lateinit var initialAsrDomainConfigurationData: AsrDomainConfigurationData
    private lateinit var asrDomainConfigurationData: AsrDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialAsrDomainConfigurationData = AsrDomainConfigurationData(
            asrDomainOption = AsrDomainOption.Disabled,
            isUseSpeechToTextMqttSilenceDetection = true
        )

        asrDomainConfigurationData = AsrDomainConfigurationData(
            asrDomainOption = AsrDomainOption.Rhasspy2HermesMQTT,
            isUseSpeechToTextMqttSilenceDetection = false
        )

        asrDomainConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialAsrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)

        with(asrDomainConfigurationData) {
            asrDomainConfigurationViewModel.onEvent(SelectAsrOptionDomain(asrDomainOption))
            asrDomainConfigurationViewModel.onEvent(SetUseAsrMqttSilenceDetectionDomain(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(asrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)

        asrDomainConfigurationViewModel.onEvent(Save)

        assertEquals(asrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(asrDomainConfigurationData, AsrDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialAsrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)

        with(asrDomainConfigurationData) {
            asrDomainConfigurationViewModel.onEvent(SelectAsrOptionDomain(asrDomainOption))
            asrDomainConfigurationViewModel.onEvent(SetUseAsrMqttSilenceDetectionDomain(isUseSpeechToTextMqttSilenceDetection))
        }

        assertEquals(asrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)

        asrDomainConfigurationViewModel.onEvent(Discard)

        assertEquals(initialAsrDomainConfigurationData, asrDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(initialAsrDomainConfigurationData, AsrDomainConfigurationData())
    }
}