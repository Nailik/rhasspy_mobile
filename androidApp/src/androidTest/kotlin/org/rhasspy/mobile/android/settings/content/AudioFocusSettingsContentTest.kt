package org.rhasspy.mobile.android.settings.content

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
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Change.SelectAudioFocusOption
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AudioFocusSettingsContentTest : KoinComponent {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<AudioFocusSettingsViewModel>()


    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController,
                LocalViewModelFactory provides get()
            ) {
                AudioFocusSettingsContent()
            }
        }

    }

    /**
     * option disable is set
     * additional settings invisible
     *
     * user clicks pause resume
     * new option is selected
     * option is saved
     * additional settings visible
     *
     * user clicks duck
     * new option is selected
     * option is saved
     * additional settings visible
     *
     * notification unchecked
     * user clicks notification
     * notification saved
     *
     * sound unchecked
     * user clicks sound
     * sound saved
     *
     * record unchecked
     * user clicks record
     * record saved
     *
     * dialog unchecked
     * user clicks dialog
     * dialog saved
     */
    @Test
    fun testContent() = runTest {
        viewModel.onEvent(SelectAudioFocusOption(AudioFocusOption.Disabled))
        assertEquals(AudioFocusOption.Disabled, AppSetting.audioFocusOption.value)

        //option disable is set
        composeTestRule.onNodeWithTag(AudioFocusOption.Disabled).onListItemRadioButton().assertIsSelected()

        //additional settings invisible
        composeTestRule.onNodeWithTag(TestTag.AudioFocusSettingsConfiguration).assertDoesNotExist()

        //user clicks pause resume
        composeTestRule.onNodeWithTag(AudioFocusOption.PauseAndResume).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        //new option is selected
        composeTestRule.onNodeWithTag(AudioFocusOption.PauseAndResume).onListItemRadioButton().assertIsSelected()
        //option is saved
        assertEquals(AudioFocusOption.PauseAndResume, AppSetting.audioFocusOption.value)
        //additional settings visible
        composeTestRule.onNodeWithTag(TestTag.AudioFocusSettingsConfiguration).assertIsDisplayed()

        //user clicks duck
        composeTestRule.onNodeWithTag(AudioFocusOption.Duck).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        //new option is selected
        composeTestRule.onNodeWithTag(AudioFocusOption.Duck).onListItemRadioButton().assertIsSelected()
        //option is saved
        assertEquals(AudioFocusOption.Duck, AppSetting.audioFocusOption.value)
        //additional settings visible
        composeTestRule.onNodeWithTag(TestTag.AudioFocusSettingsConfiguration).assertIsDisplayed()

        //notification unchecked
        assertFalse { AppSetting.isAudioFocusOnNotification.value }
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnNotification).performScrollTo().assertIsOff()
        //user clicks notification
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnNotification).performClick()
        composeTestRule.awaitIdle()
        //notification saved
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnNotification).assertIsOn()
        composeTestRule.awaitIdle()
        assertTrue { AppSetting.isAudioFocusOnNotification.value }

        //sound unchecked
        assertFalse { AppSetting.isAudioFocusOnSound.value }
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnSound).performScrollTo().assertIsOff()
        //user clicks sound
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnSound).performClick()
        composeTestRule.awaitIdle()
        //sound saved
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnSound).assertIsOn()
        assertTrue { AppSetting.isAudioFocusOnSound.value }

        //record unchecked
        assertFalse { AppSetting.isAudioFocusOnRecord.value }
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnRecord).performScrollTo().assertIsOff()
        //user clicks record
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnRecord).performClick()
        composeTestRule.awaitIdle()
        //record saved
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnRecord).assertIsOn()
        assertTrue { AppSetting.isAudioFocusOnRecord.value }

        //dialog unchecked
        assertFalse { AppSetting.isAudioFocusOnDialog.value }
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnDialog).performScrollTo().assertIsOff()
        //user clicks dialog
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnDialog).performClick()
        composeTestRule.awaitIdle()
        //dialog saved
        composeTestRule.onNodeWithTag(TestTag.AudioFocusOnDialog).assertIsOn()
        assertTrue { AppSetting.isAudioFocusOnDialog.value }
    }

}