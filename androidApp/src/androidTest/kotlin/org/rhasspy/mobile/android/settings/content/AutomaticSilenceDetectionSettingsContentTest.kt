package org.rhasspy.mobile.android.settings.content

import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.requestMicrophonePermissions
import org.rhasspy.mobile.viewModels.settings.AutomaticSilenceDetectionSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AutomaticSilenceDetectionSettingsContentTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = AutomaticSilenceDetectionSettingsViewModel()

    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalMainNavController provides navController,
                LocalSnackbarHostState provides snackbarHostState
            ) {
                AutomaticSilenceDetectionSettingsContent(viewModel)
            }
        }

    }

    /**
     * Automatic silence detection disabled
     * settings not visible
     *
     * user clicks automatic silence detection
     * Automatic silence detection enabled
     * Automatic silence detection enabled saved
     * settings visible
     *
     * user changes silence detection time to 5000
     * silence detection time 5000 saved
     */
    @Test
    fun testContent() = runBlocking {
        viewModel.toggleAutomaticSilenceDetectionEnabled(false)

        val numberInputTest = "5000"

        //Automatic silence detection disabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)
            .assertDoesNotExist()

        //user clicks automatic silence detection
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).performClick()
        //Automatic silence detection enabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).assertIsOn()
        //Automatic silence detection enabled saved
        assertTrue { AutomaticSilenceDetectionSettingsViewModel().isAutomaticSilenceDetectionEnabled.value }
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)
            .assertIsDisplayed()

        //user changes silence detection time to 5000
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTime)
            .performTextReplacement(numberInputTest)
        composeTestRule.awaitIdle()
        assertEquals(numberInputTest, viewModel.automaticSilenceDetectionTimeText.value)
        //silence detection time 5000 saved
        assertEquals(
            numberInputTest,
            AutomaticSilenceDetectionSettingsViewModel().automaticSilenceDetectionTimeText.value
        )
    }

    /**
     * Automatic silence detection enabled
     * audio level indication invisible
     *
     * user clicks audio level test
     * audio level indication shown
     * audio recording true
     *
     * user clicks stop test
     * audio level indication invisible
     * audio recording false
     */
    @Test
    fun testRecording() = runBlocking {
        requestMicrophonePermissions()

        //Automatic silence detection enabled
        viewModel.toggleAutomaticSilenceDetectionEnabled(true)
        //audio level indication invisible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertDoesNotExist()

        //user clicks audio level test
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTest).performClick()
        composeTestRule.awaitIdle()
        //audio level indication shown
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertIsDisplayed()
        //audio recording true
        assertTrue { viewModel.isRecording.value }

        //user clicks stop test
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTest).performClick()
        composeTestRule.awaitIdle()
        //audio level indication invisible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertDoesNotExist()
        //audio recording false
        assertFalse { viewModel.isRecording.value }
    }
}