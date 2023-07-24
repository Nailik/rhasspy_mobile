package org.rhasspy.mobile.android.settings.content

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.SilenceDetectionSettingsContent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.requestMicrophonePermissions
import org.rhasspy.mobile.android.utils.waitUntilExists
import org.rhasspy.mobile.app.MainActivity
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.SetSilenceDetectionEnabled
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SilenceDetectionSettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = get<SilenceDetectionSettingsViewModel>()

    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            TestContentProvider {
                SilenceDetectionSettingsContent()
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
    fun testContent() = runTest {
        viewModel.onEvent(SetSilenceDetectionEnabled(false))

        val numberInputTest = "5000"

        //Automatic silence detection disabled
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)
            .assertDoesNotExist()

        //user clicks automatic silence detection
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).performClick()
        //Automatic silence detection enabled
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.EnabledSwitch).onListItemSwitch().assertIsOn()
        //Automatic silence detection enabled saved
        assertTrue { AppSetting.isAutomaticSilenceDetectionEnabled.value }
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsConfiguration)
            .assertIsDisplayed()

        //user changes minimum silence detection time to 5000
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsMinimumTime)
            .performTextReplacement(numberInputTest)
        composeTestRule.awaitIdle()
        assertEquals(numberInputTest, viewModel.viewState.value.silenceDetectionMinimumTimeText)
        //silence detection time 5000 saved
        assertEquals(
            numberInputTest,
            AppSetting.automaticSilenceDetectionMinimumTime.value.toString()
        )

        //user changes silence detection time to 5000
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTime)
            .performTextReplacement(numberInputTest)
        composeTestRule.awaitIdle()
        assertEquals(numberInputTest, viewModel.viewState.value.silenceDetectionTimeText)
        //silence detection time 5000 saved
        assertEquals(
            numberInputTest,
            AppSetting.automaticSilenceDetectionTime.value.toString()
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
    fun testRecording() = runTest {
        get<IMicrophonePermission>().requestMicrophonePermissions()

        //Automatic silence detection enabled
        viewModel.onEvent(SetSilenceDetectionEnabled(true))
        //audio level indication invisible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertDoesNotExist()

        //user clicks audio level test
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTest)
            .performScrollTo().performClick()
        composeTestRule.awaitIdle()
        //audio level indication shown
        composeTestRule.waitUntilExists(hasTestTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest))
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertIsDisplayed()
        //audio recording true
        assertTrue { viewModel.viewState.value.isRecording }

        //user clicks stop test
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTest).performClick()
        composeTestRule.awaitIdle()
        //audio level indication invisible
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsAudioLevelTest)
            .assertDoesNotExist()
        //audio recording false
        assertFalse { viewModel.viewState.value.isRecording }
    }
}