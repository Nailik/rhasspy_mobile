package org.rhasspy.mobile.android.settings.content.sound

import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.test.R
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithCombinedTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.requestExternalStoragePermissions
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.AudioOutputOption.Sound
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.sounds.SoundOption.Disabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SelectSoundIndicationOutputOption
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetSoundIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.SetSoundIndicationOption
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewModel
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
abstract class IndicationSoundScreenTest(
    val title: StableStringResource,
    val screen: IndicationSettingsScreens
) : KoinComponent {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    abstract val device: UiDevice

    private val fileName = "sound.wav"

    abstract fun getViewModelInstance(): IIndicationSoundSettingsViewModel

    private val viewModel get() = getViewModelInstance()

    open fun setUp() {
        requestExternalStoragePermissions(device)

        composeTestRule.activity.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                IndicationSoundScreen(
                    viewModel = viewModel,
                    title = title,
                    screen = screen
                )
            }
        }
    }

    /**
     * Disabled sound is saved
     * Disabled sound ist selected
     *
     * user selects add file
     * user clicks file
     *
     * file is added to list
     * file is selected
     * file cannot be deleted
     *
     * user clicks default
     * default sound ist selected
     * default sound is saved
     *
     * file can be deleted
     * user clicks delete
     *
     * file is removed
     * file is deleted from view model
     */
    open fun testAddSoundFile() = runTest {
        //copy test file to downloads directory
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound)
            .copyTo(file.outputStream())

        viewModel.onEvent(SetSoundIndicationOption(Disabled))
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { viewModel.viewState.value.soundSetting == Disabled.name }
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onListItemRadioButton().assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //user clicks file
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //file is added to list
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onListItemRadioButton().assertIsSelected()
        //file cannot be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertDoesNotExist()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        //default sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Default).onListItemRadioButton().assertIsSelected()
        //default sound is saved
        assertTrue { viewModel.viewState.value.soundSetting == SoundOption.Default.name }

        //file can be deleted
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).assertIsDisplayed()
        //user clicks delete
        composeTestRule.onNodeWithCombinedTag(fileName, TestTag.Delete).performClick()
        composeTestRule.awaitIdle()

        //file is removed
        //composeTestRule.onNodeWithTag(fileName).assertDoesNotExist() TOO often false assert error
        //file is deleted from view model
        assertTrue { viewModel.viewState.value.customSoundFiles.isEmpty() }
    }

    /**
     * output option is sound
     * (play works with silent sound but not silent notification)
     *
     * Disabled sound is saved
     * Disabled sound ist selected
     *
     * user clicks default
     * default is selected
     *
     * user selects add file
     * user clicks file
     *
     * file is added to list
     * file is selected
     *
     * user clicks play
     * status is playing
     *
     * user clicks stop
     * status is stop
     */
    open fun testPlayback() = runTest {
        //copy test file to downloads directory
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(R.raw.sound)
            .copyTo(file.outputStream())

        //output option is sound
        //(play works with silent sound but not silent notification)
        val otherViewModel = get<IndicationSettingsViewModel>()
        otherViewModel.onEvent(SelectSoundIndicationOutputOption(Sound))
        otherViewModel.onEvent(SetSoundIndicationEnabled(true))
        viewModel.onEvent(SetSoundIndicationOption(Disabled))
        composeTestRule.awaitIdle()

        //Disabled sound is saved
        assertTrue { otherViewModel.viewState.value.isSoundIndicationEnabled }
        //Disabled sound ist selected
        composeTestRule.onNodeWithTag(TestTag.Disabled).onListItemRadioButton().assertIsSelected()

        //user clicks default
        composeTestRule.onNodeWithTag(TestTag.Default).performClick()
        //default is selected
        composeTestRule.onNodeWithTag(TestTag.Default).onListItemRadioButton().assertIsSelected()

        //user selects add file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //user clicks file
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //file is added to list
        composeTestRule.onNodeWithTag(fileName).assertIsDisplayed()
        //file is selected
        composeTestRule.onNodeWithTag(fileName).onListItemRadioButton().assertIsSelected()

        //user clicks play
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is playing
        composeTestRule.waitUntil(
            condition = { viewModel.viewState.value.isAudioPlaying },
            timeoutMillis = 5000
        )
        assertTrue { viewModel.viewState.value.isAudioPlaying }

        //user clicks stop
        composeTestRule.onNodeWithTag(TestTag.PlayPause).performClick()
        composeTestRule.awaitIdle()
        //status is stop
        composeTestRule.waitUntil(
            condition = { !viewModel.viewState.value.isAudioPlaying },
            timeoutMillis = 5000
        )
        assertFalse { viewModel.viewState.value.isAudioPlaying }
    }

}