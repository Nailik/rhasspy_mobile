package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.viewModels.configuration.AudioRecordingConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AudioRecordingConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = AudioRecordingConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                AudioRecordingConfigurationContent(viewModel)
            }
        }

    }

    /**
     * udp output option is off
     * endpoint settings not visible
     *
     * user clicks udp output option
     * output option is on
     * endpoint settings visible
     *
     * endpoint host and port can be changed
     * user click save
     * output option and endpoint host and port is saved
     */
    @Test
    fun testContent() = runBlocking {
        viewModel.toggleUdpOutputEnabled(false)
        val textInputTestHost = "hostTestInput"
        val textInputTestPort = "1556"
        //udp output option is off
        assertFalse { viewModel.isUdpOutputEnabled.value }
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpOutput).assertIsOff()
        //endpoint settings not visible
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpHost).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpPort).assertDoesNotExist()

        //user clicks udp output option
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpOutput).performClick()
        assertTrue { viewModel.isUdpOutputEnabled.value }
        //output option is on
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpOutput).assertIsOn()
        //endpoint settings visible
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpHost).assertExists()
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpPort).assertExists()

        //endpoint host and port can be changed
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpHost, true).performScrollTo().onChild().performTextReplacement(textInputTestHost)
        composeTestRule.awaitIdle()
        assertEquals(textInputTestHost, viewModel.udpOutputHost.value)
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpPort, true).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.AudioRecordingUdpPort, true).onChild().performTextReplacement(textInputTestPort)
        composeTestRule.awaitIdle()
        assertEquals(textInputTestPort, viewModel.udpOutputPort.value)
        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        //output option and endpoint host and port is saved
        val newViewModel = AudioRecordingConfigurationViewModel()
        assertEquals(true, newViewModel.isUdpOutputEnabled.value)
        assertEquals(textInputTestHost, newViewModel.udpOutputHost.value)
        assertEquals(textInputTestPort, newViewModel.udpOutputPort.value)
    }

}