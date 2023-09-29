package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingHomeAssistantOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntentHandlingConfigurationViewModelTest : AppTest() {

    private lateinit var intentHandlingConfigurationViewModel: IntentHandlingConfigurationViewModel

    private lateinit var initialIntentHandlingConfigurationData: IntentHandlingConfigurationData
    private lateinit var intentHandlingConfigurationData: IntentHandlingConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialIntentHandlingConfigurationData = IntentHandlingConfigurationData(
            intentHandlingOption = IntentHandlingOption.Disabled,
            intentHandlingHomeAssistantOption = HomeAssistantIntentHandlingOption.Intent
        )

        intentHandlingConfigurationData = IntentHandlingConfigurationData(
            intentHandlingOption = IntentHandlingOption.HomeAssistant,
            intentHandlingHomeAssistantOption = HomeAssistantIntentHandlingOption.Event
        )

        intentHandlingConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialIntentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)

        with(intentHandlingConfigurationData) {
            intentHandlingConfigurationViewModel.onEvent(SelectIntentHandlingHomeAssistantOption(intentHandlingHomeAssistantOption))
            intentHandlingConfigurationViewModel.onEvent(SelectIntentHandlingOption(intentHandlingOption))
        }

        assertEquals(intentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)

        intentHandlingConfigurationViewModel.onEvent(Save)

        assertEquals(intentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)
        assertEquals(intentHandlingConfigurationData, IntentHandlingConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialIntentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)

        with(intentHandlingConfigurationData) {
            intentHandlingConfigurationViewModel.onEvent(SelectIntentHandlingHomeAssistantOption(intentHandlingHomeAssistantOption))
            intentHandlingConfigurationViewModel.onEvent(SelectIntentHandlingOption(intentHandlingOption))
        }

        assertEquals(intentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)

        intentHandlingConfigurationViewModel.onEvent(Discard)

        assertEquals(initialIntentHandlingConfigurationData, intentHandlingConfigurationViewModel.viewState.value.editData)
        assertEquals(initialIntentHandlingConfigurationData, IntentHandlingConfigurationData())
    }
}