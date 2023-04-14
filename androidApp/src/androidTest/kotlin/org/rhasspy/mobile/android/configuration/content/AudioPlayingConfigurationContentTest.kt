package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemRadioButton
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState
import kotlin.test.assertEquals

class AudioPlayingConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = AudioPlayingConfigurationViewModel(
        service = AudioPlayingService(),
        testRunner = AudioPlayingConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                AudioPlayingConfigurationContent(viewModel)
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
    fun testEndpoint() = runBlocking {
        viewModel.onAction(SelectAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AudioPlayingOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(AudioPlayingOption.RemoteHTTP, viewState.value.audioPlayingOption)

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
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.audioPlayingHttpEndpoint)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = AudioPlayingConfigurationViewState()
        //option is saved to remote http
        assertEquals(AudioPlayingOption.RemoteHTTP, newViewState.audioPlayingOption)
        //endpoint is saved
        assertEquals(textInputTest, newViewState.audioPlayingHttpEndpoint)
        //use custom endpoint is saved
        assertEquals(true, newViewState.isUseCustomAudioPlayingHttpEndpoint)
    }

    /**
     * output is set to sound
     *
     * option disable is set
     * output options not visible
     * User clicks option local
     * new option is selected
     *
     * output options visible
     * option sound is set
     *
     * user clicks option notification
     * option notification is selected
     *
     * User clicks save
     * option is saved to local
     * option notification is saved
     */
    @Test
    fun testLocalOutput() = runBlocking {
        viewModel.onAction(SelectAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        //output is set to sound
        assertEquals(AudioOutputOption.Sound, viewState.value.audioOutputOption)

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled, true).onListItemRadioButton().assertIsSelected()
        //output options not visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()

        //User clicks option local
        composeTestRule.onNodeWithTag(AudioPlayingOption.Local).performClick()
        //new option is selected
        assertEquals(AudioPlayingOption.Local, viewState.value.audioPlayingOption)

        //output options visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        //option sound is set
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onListItemRadioButton().assertIsSelected()

        //user clicks option notification
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification).performClick()
        //option notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onListItemRadioButton().assertIsSelected()

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = AudioPlayingConfigurationViewState()
        //option is saved to local
        assertEquals(AudioPlayingOption.Local, newViewState.audioPlayingOption)
        //option notification is saved
        assertEquals(AudioOutputOption.Notification, newViewState.audioOutputOption)
    }
}