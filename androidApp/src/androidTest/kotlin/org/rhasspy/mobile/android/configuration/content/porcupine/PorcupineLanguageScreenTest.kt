package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PorcupineLanguageScreenTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<WakeWordConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                val viewState by viewModel.viewState.collectAsState()
                val contentViewState by viewState.editViewState.collectAsState()
                PorcupineLanguageScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onEvent = viewModel::onEvent
                )
            }
        }

    }

    /**
     * English is saved
     *
     * english is selected
     *
     * user clicks german
     * german is selected
     *
     * save is invoked
     * german is saved
     */
    @Test
    fun testContent() = runTest {
        //English is saved
        viewModel.onEvent(SelectWakeWordPorcupineLanguage(PorcupineLanguageOption.EN))
        viewModel.onAction(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState.value.wakeWordPorcupineViewState

        assertEquals(PorcupineLanguageOption.EN, viewState.porcupineLanguage)

        //english is selected
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.EN).onListItemRadioButton().assertIsSelected()

        //user clicks german
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).performClick()
        //german is selected
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).onListItemRadioButton().assertIsSelected()

        //save is invoked
        viewModel.onAction(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val newViewModel = WakeWordConfigurationViewModel(get(), get())
        //german is saved
        assertEquals(PorcupineLanguageOption.DE, newViewModel.viewState.value.editViewState.value.wakeWordPorcupineViewState.porcupineLanguage)
    }
}