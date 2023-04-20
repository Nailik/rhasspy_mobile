package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.utils.awaitSaved
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TextToSpeechConfigurationContentTest : KoinComponent {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<TextToSpeechConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                TextToSpeechConfigurationContent()
            }
        }

    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * Endpoint visible
     * custom endpoint switch visible
     *
     * switch is off
     * endpoint cannot be changed
     *
     * user clicks switch
     * switch is on
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     * use custom endpoint is saved
     */
    @Test
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectTextToSpeechOption(TextToSpeechOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(TextToSpeechOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(TextToSpeechOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(TextToSpeechOption.RemoteHTTP, viewState.value.textToSpeechOption)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().onListItemSwitch().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.textToSpeechHttpEndpoint)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        TextToSpeechConfigurationViewModel(get()).viewState.value.editViewState.value.also {
            //option is saved to remote http
            assertEquals(TextToSpeechOption.RemoteHTTP, it.textToSpeechOption)
            //endpoint is saved
            assertEquals(textInputTest, it.textToSpeechHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomTextToSpeechHttpEndpoint)
        }
    }

}