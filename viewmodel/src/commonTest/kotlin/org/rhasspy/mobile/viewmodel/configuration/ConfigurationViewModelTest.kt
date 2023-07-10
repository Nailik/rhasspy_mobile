package org.rhasspy.mobile.viewmodel.configuration

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.DialogAction.Confirm
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState
import kotlin.test.*

class ConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var navigator: INavigator

    private lateinit var configurationViewModel: ConfigurationViewModel

    private var onDiscard: Boolean = false
    private var onSave: Boolean = false

    private val testService = object : IService {
        override val logger = Logger.withTag("TestService")
        override val serviceState = MutableStateFlow(ServiceState.Success)
    }

    private var configurationViewState = MutableStateFlow(
        ConfigurationViewState(serviceViewState = ServiceViewState(testService.serviceState))
    )

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { navigator }
            }
        )

        configurationViewModel = object : ConfigurationViewModel(testService) {
            override fun initViewStateCreator(configurationViewState: MutableStateFlow<ConfigurationViewState>): StateFlow<ConfigurationViewState> {
                return this@ConfigurationViewModelTest.configurationViewState
            }

            override fun onDiscard() {
                onDiscard = true
            }

            override fun onSave() {
                onSave = true
            }
        }
    }

    @Test
    fun `when there are no unsaved changes and user presses back then navigator is popped back`() {
        configurationViewState.update { it.copy(hasUnsavedChanges = false) }

        configurationViewModel.onEvent(BackClick)

        verify { navigator.popBackStack() }
    }

    @Test
    fun `when there are unsaved changes and user presses back then dialog is shown`() {
        configurationViewState.update { it.copy(hasUnsavedChanges = true) }

        configurationViewModel.onEvent(BackClick)

        assertEquals(UnsavedChangesDialogState, configurationViewState.value.dialogState)
    }

    @Test
    fun `when unsaved changes dialog is shown back press will close it`() {
        assertFalse { onSave }
        assertFalse { onDiscard }
        configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

        configurationViewModel.onEvent(BackClick)

        assertEquals(null, configurationViewState.value.dialogState)
        assertFalse { onSave }
        assertFalse { onDiscard }
    }

    @Test
    fun `when unsaved changes dialog is shown and confirm is clicked then data is saved`() {
        assertFalse { onSave }
        assertFalse { onDiscard }
        configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

        configurationViewModel.onEvent(Confirm(UnsavedChangesDialogState))

        assertEquals(null, configurationViewState.value.dialogState)
        assertTrue { onSave }
        assertFalse { onDiscard }
    }

    @Test
    fun `when unsaved changes dialog is shown and dismiss is clicked then data is discarded`() {
        assertFalse { onSave }
        assertFalse { onDiscard }
        configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

        configurationViewModel.onEvent(Confirm(UnsavedChangesDialogState))

        assertEquals(null, configurationViewState.value.dialogState)
        assertFalse { onSave }
        assertTrue { onDiscard }
    }

}