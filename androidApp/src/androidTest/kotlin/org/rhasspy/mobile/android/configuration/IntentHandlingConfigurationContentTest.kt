package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.IntentHandlingConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import kotlin.test.assertEquals

class IntentHandlingConfigurationContentTest : FlakyTest() {


    private val viewModel = get<IntentHandlingConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        IntentHandlingConfigurationScreen(viewModel)
    }

    /**
     * option disable is set
     * User clicks option HomeAssistant
     * new option is selected
     *
     * send events visible
     * send intents visible
     *
     * send intents is set
     * send events can clicked
     * send events is set
     *
     * User clicks save
     * option is saved to HomeAssistant
     * send events is saved
     */
    @Test
    @AllowFlaky
    fun testHomeAssistant() = runTest {
        setupContent()

        viewModel.onEvent(SelectIntentHandlingOption(IntentHandlingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(IntentHandlingOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option HomeAssistant
        composeTestRule.onNodeWithTag(IntentHandlingOption.HomeAssistant).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            IntentHandlingOption.HomeAssistant,
            viewModel.viewState.value.editData.intentHandlingOption
        )

        //send events visible
        composeTestRule.onNodeWithTag(TestTag.SendEvents).assertExists()
        //send intents visible
        composeTestRule.onNodeWithTag(TestTag.SendIntents).assertExists()

        //send intents is set
        composeTestRule.onNodeWithTag(TestTag.SendIntents, true)
            .performScrollTo().performClick().onListItemRadioButton().assertIsSelected()
        //send events can clicked
        composeTestRule.onNodeWithTag(TestTag.SendEvents).performScrollTo().performClick()
        //send events is set
        composeTestRule.onNodeWithTag(TestTag.SendEvents, true)
            .performScrollTo().onListItemRadioButton().assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        composeTestRule.awaitIdle()
        IntentHandlingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to HomeAssistant
            assertEquals(IntentHandlingOption.HomeAssistant, it.intentHandlingOption)
            //send events is saved
            assertEquals(
                HomeAssistantIntentHandlingOption.Event,
                it.intentHandlingHomeAssistantOption
            )
        }
    }

}