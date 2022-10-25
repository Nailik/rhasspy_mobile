package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.viewModels.configuration.AudioPlayingConfigurationViewModel
import kotlin.test.assertEquals

/**
 * Options changed and saved
 */
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
     * Endpoint visible when option is remote http
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     */
    @Test
    fun testDialog() {
        viewModel.selectAudioPlayingOption(AudioPlayingOptions.Disabled)
        val textInputTest = "endpointTestInput"
        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOptions.Disabled, true).onChildAt(0).assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AudioPlayingOptions.RemoteHTTP, true).performClick()
        //new option is selected
        assertEquals(AudioPlayingOptions.RemoteHTTP, viewModel.audioPlayingOption.value)
        //Endpoint visible when option is remote http
        composeTestRule.onNodeWithTag(TestTag.AudioPlayingHttpEndpoint, true).assertExists()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.AudioPlayingHttpEndpoint, true).performScrollTo().onChild().performTextReplacement(textInputTest)
        assertEquals(textInputTest, viewModel.audioPlayingHttpEndpoint.value)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = AudioPlayingConfigurationViewModel()
        //option is saved to remote http
        assertEquals(AudioPlayingOptions.RemoteHTTP, newViewModel.audioPlayingOption.value)
        //endpoint is saved
        assertEquals(textInputTest, newViewModel.audioPlayingHttpEndpoint.value)
    }

}