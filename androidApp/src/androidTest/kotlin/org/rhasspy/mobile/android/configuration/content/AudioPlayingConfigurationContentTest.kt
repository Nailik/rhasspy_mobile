package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
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
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.onSwitch
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption
import org.rhasspy.mobile.data.serviceoption.AudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.AudioPlayingConfigurationViewModel
import kotlin.test.assertEquals

class AudioPlayingConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = AudioPlayingConfigurationViewModel()

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
        viewModel.selectAudioPlayingOption(AudioPlayingOption.Disabled)
        viewModel.onSave()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled).onChildAt(0)
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AudioPlayingOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(AudioPlayingOption.RemoteHTTP, viewModel.audioPlayingOption.value)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().onSwitch()
            .assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewModel.audioPlayingHttpEndpoint.value)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewModel = AudioPlayingConfigurationViewModel()
        //option is saved to remote http
        assertEquals(AudioPlayingOption.RemoteHTTP, newViewModel.audioPlayingOption.value)
        //endpoint is saved
        assertEquals(textInputTest, newViewModel.audioPlayingHttpEndpoint.value)
        //use custom endpoint is saved
        assertEquals(true, newViewModel.isUseCustomAudioPlayingHttpEndpoint.value)
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
        viewModel.selectAudioPlayingOption(AudioPlayingOption.Disabled)
        viewModel.onSave()

        //output is set to sound
        assertEquals(AudioOutputOption.Sound, viewModel.audioOutputOption.value)

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled, true).onChildAt(0)
            .assertIsSelected()
        //output options not visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()

        //User clicks option local
        composeTestRule.onNodeWithTag(AudioPlayingOption.Local).performClick()
        //new option is selected
        assertEquals(AudioPlayingOption.Local, viewModel.audioPlayingOption.value)

        //output options visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        //option sound is set
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onChildAt(0)
            .assertIsSelected()

        //user clicks option notification
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification).performClick()
        //option notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onChildAt(0)
            .assertIsSelected()

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewModel = AudioPlayingConfigurationViewModel()
        //option is saved to local
        assertEquals(AudioPlayingOption.Local, newViewModel.audioPlayingOption.value)
        //option notification is saved
        assertEquals(AudioOutputOption.Notification, newViewModel.audioOutputOption.value)
    }

}