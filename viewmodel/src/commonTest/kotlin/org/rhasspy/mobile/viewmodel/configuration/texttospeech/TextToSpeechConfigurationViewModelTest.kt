package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState.TextToSpeechConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextToSpeechConfigurationViewModelTest : AppTest() {

    private lateinit var textToSpeechConfigurationViewModel: TextToSpeechConfigurationViewModel

    private lateinit var initialTextToSpeechConfigurationData: TextToSpeechConfigurationData
    private lateinit var textToSpeechConfigurationData: TextToSpeechConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialTextToSpeechConfigurationData = TextToSpeechConfigurationData(
            ttsDomainOption = TtsDomainOption.Disabled,
        )

        textToSpeechConfigurationData = TextToSpeechConfigurationData(
            ttsDomainOption = TtsDomainOption.Rhasspy2HermesHttp,
        )

        textToSpeechConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialTextToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)

        with(textToSpeechConfigurationData) {
            textToSpeechConfigurationViewModel.onEvent(SelectTextToSpeechOption(ttsDomainOption))
        }

        assertEquals(textToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)

        textToSpeechConfigurationViewModel.onEvent(Save)

        assertEquals(textToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)
        assertEquals(textToSpeechConfigurationData, TextToSpeechConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialTextToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)

        with(textToSpeechConfigurationData) {
            textToSpeechConfigurationViewModel.onEvent(SelectTextToSpeechOption(ttsDomainOption))
        }

        assertEquals(textToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)

        textToSpeechConfigurationViewModel.onEvent(Discard)

        assertEquals(initialTextToSpeechConfigurationData, textToSpeechConfigurationViewModel.viewState.value.editData)
        assertEquals(initialTextToSpeechConfigurationData, TextToSpeechConfigurationData())
    }
}