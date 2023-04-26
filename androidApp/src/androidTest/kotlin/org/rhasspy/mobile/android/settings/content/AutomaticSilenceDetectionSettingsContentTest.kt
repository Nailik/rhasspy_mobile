package org.rhasspy.mobile.android.settings.content

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.requestMicrophonePermissions
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.SetSilenceDetectionEnabled
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AutomaticSilenceDetectionSettingsContentTest : KoinComponent {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = get<SilenceDetectionSettingsViewModel>()

    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            TestContentProvider {
                AutomaticSilenceDetectionSettingsContent()
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
        assertTrue { SilenceDetectionSettingsViewModel(get(), get(), get()).viewState.value.isSilenceDetectionEnabled }
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
            SilenceDetectionSettingsViewModel(get(), get(), get()).viewState.value.silenceDetectionMinimumTimeText
        )

        //user changes silence detection time to 5000
        composeTestRule.onNodeWithTag(TestTag.AutomaticSilenceDetectionSettingsTime)
            .performTextReplacement(numberInputTest)
        composeTestRule.awaitIdle()
        assertEquals(numberInputTest, viewModel.viewState.value.silenceDetectionTimeText)
        //silence detection time 5000 saved
        assertEquals(
            numberInputTest,
            SilenceDetectionSettingsViewModel(get(), get(), get()).viewState.value.silenceDetectionTimeText
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
        requestMicrophonePermissions()

        //Automatic silence detection enabled
        viewModel.onEvent(SetSilenceDetectionEnabled(true))
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