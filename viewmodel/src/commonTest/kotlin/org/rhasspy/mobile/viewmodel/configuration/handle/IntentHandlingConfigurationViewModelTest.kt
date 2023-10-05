package org.rhasspy.mobile.viewmodel.configuration.handle

import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationUiEvent.Change.SelectHandleDomainHomeAssistantOption
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationUiEvent.Change.SelectHandleDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewState.HandleDomainConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntentHandlingConfigurationViewModelTest : AppTest() {

    private lateinit var handleDomainConfigurationViewModel: HandleDomainConfigurationViewModel

    private lateinit var initialHandleDomainConfigurationData: HandleDomainConfigurationData
    private lateinit var handleDomainConfigurationData: HandleDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialHandleDomainConfigurationData = HandleDomainConfigurationData(
            handleDomainOption = HandleDomainOption.Disabled,
            intentHandlingHomeAssistantOption = HomeAssistantIntentHandlingOption.Intent
        )

        handleDomainConfigurationData = HandleDomainConfigurationData(
            handleDomainOption = HandleDomainOption.HomeAssistant,
            intentHandlingHomeAssistantOption = HomeAssistantIntentHandlingOption.Event
        )

        handleDomainConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialHandleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)

        with(handleDomainConfigurationData) {
            handleDomainConfigurationViewModel.onEvent(SelectHandleDomainHomeAssistantOption(intentHandlingHomeAssistantOption))
            handleDomainConfigurationViewModel.onEvent(SelectHandleDomainOption(handleDomainOption))
        }

        assertEquals(handleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)

        handleDomainConfigurationViewModel.onEvent(Save)

        assertEquals(handleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(handleDomainConfigurationData, HandleDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialHandleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)

        with(handleDomainConfigurationData) {
            handleDomainConfigurationViewModel.onEvent(SelectHandleDomainHomeAssistantOption(intentHandlingHomeAssistantOption))
            handleDomainConfigurationViewModel.onEvent(SelectHandleDomainOption(handleDomainOption))
        }

        assertEquals(handleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)

        handleDomainConfigurationViewModel.onEvent(Discard)

        assertEquals(initialHandleDomainConfigurationData, handleDomainConfigurationViewModel.viewState.value.editData)
        assertEquals(initialHandleDomainConfigurationData, HandleDomainConfigurationData())
    }
}