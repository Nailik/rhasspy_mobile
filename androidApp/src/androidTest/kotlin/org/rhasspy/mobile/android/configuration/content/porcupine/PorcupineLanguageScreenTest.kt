package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.onListItemRadioButton
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState
import kotlin.test.assertEquals

class PorcupineLanguageScreenTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel(
        service = WakeWordService(),
        testRunner = WakeWordConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                val viewState by viewModel.viewState.collectAsState()
                val contentViewState by viewState.editViewState.collectAsState()
                PorcupineLanguageScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onAction = viewModel::onAction
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
    fun testContent() = runBlocking {
        //English is saved
        viewModel.onAction(SelectWakeWordPorcupineLanguage(PorcupineLanguageOption.EN))
        viewModel.onSave()
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
        viewModel.onSave()
        val newViewState = PorcupineViewState()
        //german is saved
        assertEquals(PorcupineLanguageOption.DE, newViewState.porcupineLanguage)
    }
}