package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.nVerify
import org.rhasspy.mobile.viewmodel.configuration.connections.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.ConfigurationViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.DialogAction.Confirm
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.DialogAction.Dismiss
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import kotlin.test.*

class ConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var navigator: INavigator

    private lateinit var configurationViewModel: ConfigurationViewModel

    private var onDiscard: Boolean = false
    private var onSave: Boolean = false

    private val testService = object : IDomain {
        val connectionState = MutableStateFlow(ConnectionState.Success)
    }

    private lateinit var configurationViewState: MutableStateFlow<ConfigurationViewState>
    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { navigator }
            }
        )

        configurationViewModel = object : ConfigurationViewModel(testService.connectionState) {
            override fun initViewStateCreator(configurationViewState: MutableStateFlow<ConfigurationViewState>): StateFlow<ConfigurationViewState> {
                this@ConfigurationViewModelTest.configurationViewState = configurationViewState
                return configurationViewState
            }

            init {
                configurationViewState
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
    fun `when there are no unsaved changes and user presses back then navigator is popped back`() =
        runTest {
            every { navigator.onBackPressed() } returns Unit
            every { navigator.popBackStack() } returns Unit
            configurationViewState.update { it.copy(hasUnsavedChanges = false) }

            configurationViewModel.onEvent(BackClick)

            nVerify { navigator.onBackPressed() }
        }

    @Test
    fun `when there are unsaved changes and user presses back then dialog is shown`() = runTest {
        every { navigator.onBackPressed() } returns Unit
        configurationViewState.update { it.copy(hasUnsavedChanges = true) }

        configurationViewModel.onEvent(BackClick)

        assertEquals(null, configurationViewModel.configurationViewState.value.dialogState)
    }

    @Test
    fun `when unsaved changes dialog is shown back press will not close it`() = runTest {
        every { navigator.onBackPressed() } returns Unit
        assertFalse { onSave }
        assertFalse { onDiscard }
        configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

        configurationViewModel.onEvent(BackClick)

        assertEquals(
            UnsavedChangesDialogState,
            configurationViewModel.configurationViewState.value.dialogState
        )
        assertFalse { onSave }
        assertFalse { onDiscard }
    }

    @Test
    fun `when unsaved changes dialog is shown and confirm is clicked then data is saved`() =
        runTest(get<IDispatcherProvider>().IO) {
            every { navigator.popBackStack() } returns Unit
            assertFalse { onSave }
            assertFalse { onDiscard }
            configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

            configurationViewModel.onEvent(Confirm(UnsavedChangesDialogState))

            assertEquals(null, configurationViewModel.configurationViewState.value.dialogState)
            assertTrue { onSave }
            assertFalse { onDiscard }
        }

    @Test
    fun `when unsaved changes dialog is shown and dismiss is clicked then data is discarded`() =
        runTest {
            every { navigator.popBackStack() } returns Unit
            assertFalse { onSave }
            assertFalse { onDiscard }
            configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }

            configurationViewModel.onEvent(Dismiss(UnsavedChangesDialogState))

            assertEquals(null, configurationViewModel.configurationViewState.value.dialogState)
            assertFalse { onSave }
            assertTrue { onDiscard }
        }

}