package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.tts.TtsDomainConfigurationUiEvent.Change.SelectTtsDomainOption
import org.rhasspy.mobile.viewmodel.configuration.tts.TtsDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.tts.TtsDomainConfigurationViewState.TtsDomainConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextToSpeechConfigurationViewModelTest : AppTest() {

    private lateinit var ttsDomainConfigurationViewModel: TtsDomainConfigurationViewModel

    private lateinit var initialTtsDomainConfigurationData: TtsDomainConfigurationData
    private lateinit var ttsDomainConfigurationData: TtsDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialTtsDomainConfigurationData = TtsDomainConfigurationData(
            ttsDomainOption = TtsDomainOption.Disabled,
        )

        ttsDomainConfigurationData = TtsDomainConfigurationData(
            ttsDomainOption = TtsDomainOption.Rhasspy2HermesHttp,
        )

        ttsDomainConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialTtsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)

        with(ttsDomainConfigurationData) {
            ttsDomainConfigurationViewModel.onEvent(SelectTtsDomainOption(ttsDomainOption))
        }

        assertEquals(ttsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)

        ttsDomainConfigurationViewModel.onEvent(Save)

        assertEquals(ttsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(ttsDomainConfigurationData, TtsDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialTtsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)

        with(ttsDomainConfigurationData) {
            ttsDomainConfigurationViewModel.onEvent(SelectTtsDomainOption(ttsDomainOption))
        }

        assertEquals(ttsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)

        ttsDomainConfigurationViewModel.onEvent(Discard)

        assertEquals(initialTtsDomainConfigurationData, ttsDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(initialTtsDomainConfigurationData, TtsDomainConfigurationData())
    }
}