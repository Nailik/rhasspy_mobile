package org.rhasspy.mobile.android.configuration.porcupine

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import kotlin.test.assertEquals

class PorcupineLanguageScreenTest : FlakyTest() {

    private val viewModel = get<WakeWordConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        PorcupineLanguageScreen()
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
    @AllowFlaky
    fun testContent() = runTest {
        setupContent()

        //English is saved
        viewModel.onEvent(SelectWakeWordPorcupineLanguage(PorcupineLanguageOption.EN))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()
        val editData = viewModel.viewState.value.editData.wakeWordPorcupineConfigurationData

        composeTestRule.awaitIdle()
        assertEquals(PorcupineLanguageOption.EN, editData.porcupineLanguage)

        //english is selected
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.EN).onListItemRadioButton()
            .assertIsSelected()

        //user clicks german
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).performClick()
        //german is selected
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).onListItemRadioButton()
            .assertIsSelected()
    }
}